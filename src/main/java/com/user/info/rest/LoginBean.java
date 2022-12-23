package com.user.info.rest;


import java.util.Set;
import java.util.HashSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import com.ibm.websphere.security.jwt.JwtBuilder;
import com.user.info.rest.util.SessionUtils;
import com.ibm.websphere.security.jwt.Claims;
 
 
// tag::loginBean[]
@ApplicationScoped
@Named
public class LoginBean {

    private String username;
    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // tag::doLogin[]
    public String doLogin() throws Exception {
        HttpServletRequest request = SessionUtils.getRequest();
        
        System.out.println("Just for commit...");
        
        try {
            request.logout();
            request.login(this.username, this.password);
        } catch (ServletException e) {
            System.out.println("Login failed.");
            return "error.jsf";
        }

        String remoteUser = request.getRemoteUser();
        Set<String> roles = getRoles(request);
        if (remoteUser != null && remoteUser.equals(username)) {
            String jwt = buildJwt(username, roles);
            HttpSession ses = request.getSession();
            if (ses == null) {
                System.out.println("Session timed out.");
            } else {
                // tag::setAttribute[]
                ses.setAttribute("jwt", jwt);
                // end::setAttribute[]
            }
        } else {
            System.out.println("Failed to update JWT in session.");
        }
        return "application.jsf?faces-redirect=true";
    }
    // end::doLogin[]
    // tag::buildJwt[]

  private String buildJwt(String userName, Set<String> roles) throws Exception {
        // tag::jwtBuilder[]
        return JwtBuilder.create("jwtFrontEndBuilder")
        // end::jwtBuilder[]
                         .claim(Claims.SUBJECT, userName)
                         .claim("upn", userName)
                         // tag::claim[]
                         .claim("groups", roles.toArray(new String[roles.size()]))
                         .claim("aud", "systemService")
                         // end::claim[]
                         .buildJwt()
                         .compact();

    }
    // end::buildJwt[]

    private Set<String> getRoles(HttpServletRequest request) {
        Set<String> roles = new HashSet<String>();
        boolean isAdmin = request.isUserInRole("admin");
        boolean isUser = request.isUserInRole("user");
        if (isAdmin) {
            roles.add("admin");
        }
        if (isUser) {
            roles.add("user");
        }
        return roles;
    }
}
// end::loginBean[]
