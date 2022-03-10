import { Button, Container } from "@material-ui/core";
import React from "react";
import TestLogin from "./TestLogin";

export const OAUTH_LOGIN_URL = "/api/oauth/login/okta";

function login() {
  window.location.href = OAUTH_LOGIN_URL; // /keycloak
}

class Login extends React.Component {
  render(): React.ReactNode {
    return (
      <Container style={{ height: "100%", textAlign: "center" }}>
        <p style={{ paddingTop: "50px", marginTop: "0px" }}>
          Share your location with selected contacts.
          <br />
        </p>
        <Button variant="outlined" style={{ margin: "50px" }} onClick={login}>
          Login
        </Button>
        <br />
        <TestLogin />
      </Container>
    );
  }
}

export default Login;
