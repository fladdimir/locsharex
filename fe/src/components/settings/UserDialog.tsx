import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  TextField,
} from "@material-ui/core";
import DeleteIcon from "@material-ui/icons/DeleteForever";
import FaceIcon from "@material-ui/icons/Face";
import React from "react";
import { Contact } from "../service/Contact";
import { fetchX } from "../service/fetchX";

interface Props {
  open: boolean;
  onConfirm: () => void; // take over new user for update?
  onAbort: () => void;
  user: Contact;
}

interface State {
  chosenUserName: string;
  isValid: boolean;
  deletionMode: boolean;
}

interface ValidityResult {
  valid: boolean;
}

async function checkValidity(name: string): Promise<boolean> {
  const url = `/api/users/check-name?name=${name}`;
  const validityResult: ValidityResult = await fetchX(url, "GET");
  return validityResult.valid;
}

async function updateSimpleUserData(user: Contact): Promise<Contact> {
  const url = `/api/users/simple-data`;
  return fetchX(url, "PUT", user);
}

async function deleteUser(user: Contact): Promise<Contact> {
  const url = "/api/users/" + user.id;
  return fetchX(url, "DELETE");
}

class UserDialog extends React.Component<Props, State> {
  // assign random user-name on signup, changeable here...
  state: State = {
    chosenUserName: this.props.user.name,
    isValid: false,
    deletionMode: false,
  };

  componentDidMount() {
    this.reset();
  }

  changeUserName(chosenUserName: string) {
    this.setState({ chosenUserName });
    if (
      chosenUserName &&
      chosenUserName !== this.props.user.name &&
      /^[A-Za-z0-9]{3,}$/.test(chosenUserName)
    ) {
      this.checkValidity(chosenUserName);
    } else {
      this.setState({ isValid: false });
    }
  }

  async checkValidity(chosenUserName: string) {
    const isValid = await checkValidity(chosenUserName);
    this.setState({ isValid });
  }

  abort() {
    this.reset();
    this.props.onAbort();
  }

  async confirm() {
    const user = { ...this.props.user };
    user.name = this.state.chosenUserName;
    await updateSimpleUserData(user);
    this.props.onConfirm();
  }

  reset() {
    this.setState({
      chosenUserName: this.props.user.name,
      isValid: false,
      deletionMode: false,
    });
  }

  async deleteUser() {
    await deleteUser(this.props.user);
    window.location.href = "/api/app-logout";
  }

  render(): React.ReactNode {
    return (
      <Dialog onClose={() => this.abort()} open={this.props.open}>
        <DialogTitle style={{ position: "relative" }}>
          <FaceIcon style={{ margin: "-10px", marginLeft: "0px" }} />
          {!this.state.deletionMode ? (
            <IconButton
              style={{ position: "absolute", right: "10px" }}
              onClick={() => this.setState({ deletionMode: true })}
            >
              <DeleteIcon />
            </IconButton>
          ) : (
            <Button
              style={{ position: "absolute", right: "15px" }}
              variant="contained"
              color="secondary"
              onClick={() => this.deleteUser()}
            >
              Delete profile
            </Button>
          )}
        </DialogTitle>
        <DialogContent style={{ display: "flex", flexDirection: "column" }}>
          <TextField
            label="Username"
            value={this.state?.chosenUserName}
            onChange={(event) => this.changeUserName(event.target.value)}
          ></TextField>
        </DialogContent>
        <Divider />
        <DialogActions>
          <Button
            autoFocus
            onClick={() => this.confirm()}
            color="primary"
            disabled={!this.state.isValid}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    );
  }
}

export default UserDialog;
