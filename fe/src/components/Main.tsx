import { Container } from "@material-ui/core";
import React from "react";
import AppMap from "./map/AppMap";
import Menu, { MenuItem } from "./Menu";
import SettingsComponent from "./settings/SettingsComponent";

interface Props {
  userId: number;
}

interface State {
  selected: MenuItem;
}

class Main extends React.Component<Props, State> {
  state: State = { selected: MenuItem.MAP };

  onMenuItemSelectionChange = (selected: MenuItem) => {
    this.setState({ selected });
  };

  render(): React.ReactNode {
    return (
      <Container maxWidth="xl" style={{ height: "100%", padding: "1%" }}>
        {this.state.selected === MenuItem.MAP ? (
          <AppMap userId={this.props.userId} style={{ height: "95%" }} />
        ) : (
          <SettingsComponent
            userId={this.props.userId}
            style={{ height: "95%" }}
          />
        )}

        <Menu
          style={{ height: "10%" }}
          selected={this.state.selected}
          onSelectionChange={this.onMenuItemSelectionChange}
        />
      </Container>
    );
  }
}

export default Main;
