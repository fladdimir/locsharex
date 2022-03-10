import { Button } from "@material-ui/core";
import React from "react";

function login(username: string): void {
  fetch("/api/login", {
    method: "POST",
    body: JSON.stringify({
      username, //
      password: "password",
    }),
    headers: { "Content-Type": "application/json" },
  }).then((response) => {
    if (response.status == 200 && response.redirected) {
      window.location.assign(response.url);
    } else {
      console.error(JSON.stringify(response, undefined, 4));
      alert("authentication failure");
    }
  });
}

async function checkIsEnabled(): Promise<boolean> {
  return fetch("api/login").then((response) => {
    return 405 === response.status; // enabled, only POST is allowed
  });
}

interface Props {}

interface State {
  isEnabled: boolean;
}

class TestLogin extends React.Component<Props, State> {
  _isMounted: boolean = false;

  state: State = {
    isEnabled: false,
  };

  componentDidMount(): void {
    this._isMounted = true;
    this.checkIsEnabled();
  }

  componentWillUnmount(): void {
    this._isMounted = false;
  }

  async checkIsEnabled(): Promise<void> {
    const isEnabled: boolean = await checkIsEnabled();
    if (this._isMounted) this.setState({ isEnabled });
  }

  render(): React.ReactNode {
    return this.state.isEnabled ? (
      <React.Fragment>
        <Button
          id={"login-sherlock"}
          variant="outlined"
          style={{ margin: "50px" }}
          onClick={() => login("Sherlock")}
        >
          Test as Sherlock
        </Button>
        <Button
          id={"login-watson"}
          variant="outlined"
          style={{ margin: "50px" }}
          onClick={() => login("Watson")}
        >
          Test as Watson
        </Button>
        <Button
          id={"login-lestrade"}
          variant="outlined"
          style={{ margin: "50px" }}
          onClick={() => login("Lestrade")}
        >
          Test as Lestrade
        </Button>
      </React.Fragment>
    ) : null;
  }
}

export default TestLogin;
