import { Typography } from "@material-ui/core";
import Checkbox from "@material-ui/core/Checkbox";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import React from "react";
import { Contact } from "../service/Contact";

interface Props {
  label: string;
  items: Contact[];
  filter: string;
  checked: boolean;
  onClick: (contact: Contact) => void;
}

class ContactList extends React.Component<Props> {
  render(): React.ReactNode {
    return this.props.items.length ? (
      <React.Fragment>
        <Typography variant="caption" color="primary">
          {this.props.label}:
        </Typography>

        <List style={{ paddingTop: "0px", paddingBottom: "20px" }}>
          {this.props.items
            .filter((item) =>
              item.name.toLowerCase().includes(this.props.filter.toLowerCase())
            )
            .map((item) => (
              <ListItem
                key={item.id}
                role={undefined}
                dense
                button
                onClick={() => this.props.onClick(item)}
              >
                <ListItemIcon>
                  <Checkbox
                    edge="start"
                    checked={this.props.checked}
                    tabIndex={-1}
                    disableRipple
                  />
                </ListItemIcon>
                <ListItemText primary={item.name} />
              </ListItem>
            ))}
        </List>
      </React.Fragment>
    ) : null;
  }
}

export default ContactList;
