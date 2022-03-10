import CircularProgress from "@material-ui/core/CircularProgress";
import React from "react";
import Login from "./login/Login";
import Main from "./Main";
import { fetchX } from "./service/fetchX";

interface Props {}

interface State {
  isLoading: boolean;
  userId?: number; // undefined if logged out
}

async function fetchUserId(): Promise<number | undefined> {
  const url = "/api/users/login-info";
  const result = await fetchX(url, "GET");
  return result?.userId;
}

class Start extends React.Component<Props, State> {
  state: State = { isLoading: false, userId: undefined };

  componentDidMount() {
    this.fetchUserId();
  }

  async fetchUserId() {
    this.setState({ isLoading: true });
    const userId = await fetchUserId();
    this.setState({ userId, isLoading: false });
  }

  render(): React.ReactNode {
    if (this.state.isLoading)
      return (
        <CircularProgress
          style={{ position: "fixed", top: "50%", left: "50%" }}
        />
      );
    return this.state.userId ? <Main userId={this.state.userId} /> : <Login />;
  }
}

export default Start;
