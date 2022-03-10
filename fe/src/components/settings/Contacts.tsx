import TextField from "@material-ui/core/TextField";
import React from "react";
import { Contact } from "../service/Contact";
import { UserWithContacts } from "../service/UserWithContacts";
import ContactList from "./ContactList";
import { fetchX } from "../service/fetchX";

interface Props {
  user: UserWithContacts;
}

interface State {
  sharedWith: Contact[];
  sharedBy: Contact[];
  filter: string;
  others: Contact[];
}

async function requestShareWith(
  id: number,
  contactId: number
): Promise<UserWithContacts> {
  const url = `/api/users/${id}/share-with/${contactId}`;
  return fetchX(url, "PUT");
}

async function requestStopShareWith(
  id: number,
  contactId: number
): Promise<UserWithContacts> {
  const url = `/api/users/${id}/share-with/${contactId}`;
  return fetchX(url, "DELETE");
}

async function requestSearchByName(name: string): Promise<Array<Contact>> {
  const url = `/api/users/search?name=${name}`;
  return fetchX(url, "GET");
}

const compareContactNames = (a: Contact, b: Contact) =>
  a.name.localeCompare(b.name);

class Contacts extends React.Component<Props, State> {
  state: State = {
    sharedWith: [],
    sharedBy: [],
    filter: "",
    others: [],
  };

  componentDidMount() {
    this.updateStateFromUserResponse(this.props.user);
  }

  getId(): any {
    return this.props.user.id;
  }

  updateStateFromUserResponse(user: UserWithContacts) {
    user.sharedWith = user.sharedWith ?? [];
    user.sharedBy = user.sharedBy ?? [];
    this.setState({
      sharedWith: user.sharedWith
        .filter((w) => w.id !== this.getId())
        .sort(compareContactNames),
      sharedBy: user.sharedBy
        .filter((b) => b.id !== this.getId())
        .sort(compareContactNames),
    });
  }

  async shareWith(contact: Contact) {
    const user = await requestShareWith(this.getId(), contact.id);
    this.updateStateFromUserResponse(user);
  }

  async stopShareWith(contact: Contact) {
    const user = await requestStopShareWith(this.getId(), contact.id);
    this.updateStateFromUserResponse(user);
  }

  async search(name: string) {
    if (!name || !name.trim()) {
      this.setState({ others: [] });
      return;
    }
    const others = await requestSearchByName(name);
    this.setState({
      others,
    });
  }

  changeFilter = (newValue: string) => {
    this.setState({
      filter: newValue,
    });
    this.search(newValue);
  };

  getContacts = () => {
    const res = this.state.sharedWith.filter((w) =>
      this.state.sharedBy.map((b) => b.id).includes(w.id)
    );
    return this.filter(res);
  };

  getPending = () => {
    const res = this.state.sharedWith.filter(
      (w) => !this.state.sharedBy.map((b) => b.id).includes(w.id)
    );
    return this.filter(res);
  };

  getReadyToShare = () => {
    const res = this.state.sharedBy.filter(
      (b) => !this.state.sharedWith.map((w) => w.id).includes(b.id)
    );
    return this.filter(res);
  };

  getOthers = () => {
    return this.state.others.filter(
      (o) =>
        !this.state.sharedBy.map((b) => b.id).includes(o.id) &&
        !this.state.sharedWith.map((w) => w.id).includes(o.id) &&
        o.id !== this.getId()
    );
  };

  filter = (contacts: Contact[]) => {
    return contacts.filter((c) =>
      c.name.toLowerCase().includes(this.state.filter.toLowerCase())
    );
  };

  render(): React.ReactNode {
    return (
      <React.Fragment>
        <div style={{ padding: "20px", paddingLeft: "0px" }}>
          <TextField
            label="Share with..."
            value={this.state.filter}
            onChange={(event) => this.changeFilter(event.target.value)}
          ></TextField>
        </div>
        <ContactList
          label="Contacts"
          items={this.getContacts()}
          filter={this.state.filter}
          checked={true}
          onClick={(c) => this.stopShareWith(c)}
        />
        <ContactList
          filter={this.state.filter}
          label="Ready to share"
          items={this.getReadyToShare()}
          checked={false}
          onClick={(c) => this.shareWith(c)}
        />
        <ContactList
          label="Pending"
          items={this.getPending()}
          filter={this.state.filter}
          checked={true}
          onClick={(c) => this.stopShareWith(c)}
        />
        <ContactList
          label="Others"
          items={this.getOthers()}
          filter={this.state.filter}
          checked={false}
          onClick={(c) => this.shareWith(c)}
        />
      </React.Fragment>
    );
  }
}

export default Contacts;
