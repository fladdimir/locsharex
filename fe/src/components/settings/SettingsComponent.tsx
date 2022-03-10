import { Grid, IconButton, Paper } from "@material-ui/core";
import CardContent from "@material-ui/core/CardContent";
import Divider from "@material-ui/core/Divider";
import ExitToAppSharpIcon from "@material-ui/icons/ExitToAppSharp";
import FaceIcon from "@material-ui/icons/Face";
import React from "react";
import { UserWithContacts } from "../service/UserWithContacts";
import Contacts from "./Contacts";
import { fetchX } from "../service/fetchX";
import UserDialog from "./UserDialog";

interface Props {
  style: React.CSSProperties;
  userId: number;
}

interface State {
  user?: UserWithContacts;
  openUserDialog: boolean;
}

async function fetchUserWithContacts(id: number): Promise<UserWithContacts> {
  const url = `/api/users/${id}`;
  return fetchX(url, "GET");
}

class SettingsComponent extends React.Component<Props, State> {
  state: State = {
    user: undefined,
    openUserDialog: false,
  };

  componentDidMount() {
    this.fetchUserWithContacts(this.props.userId);
  }

  async fetchUserWithContacts(id: number) {
    const user = await fetchUserWithContacts(id);
    this.setState({ user });
  }

  render(): React.ReactNode {
    return (
      <React.Fragment>
        <Paper elevation={0} square={true} style={this.props.style}>
          <CardContent>
            <Grid style={{ display: "flex", position: "relative" }}>
              <IconButton
                onClick={() => this.setState({ openUserDialog: true })}
              >
                <FaceIcon />
              </IconButton>
              <p
                onClick={() => this.setState({ openUserDialog: true })}
                style={{ paddingTop: "5px" }}
              >
                {this.state.user?.name}
              </p>
              <a href="/api/app-logout">
                <IconButton
                  style={{ position: "absolute", right: "0px" }}
                  id={"logoutButton"}
                >
                  <ExitToAppSharpIcon />
                </IconButton>
              </a>
            </Grid>
            <Divider />
            {this.state.user ? <Contacts user={this.state.user} /> : null}
          </CardContent>
        </Paper>
        {this.state.user ? (
          <UserDialog
            open={this.state.openUserDialog}
            onConfirm={() => {
              this.fetchUserWithContacts(this.props.userId);
              this.setState({ openUserDialog: false });
            }}
            onAbort={() => this.setState({ openUserDialog: false })}
            user={this.state.user}
          />
        ) : null}
      </React.Fragment>
    );
  }
}

export default SettingsComponent;
