import BottomNavigation, {
  BottomNavigationProps,
} from "@material-ui/core/BottomNavigation";
import BottomNavigationAction from "@material-ui/core/BottomNavigationAction";
import MapIcon from "@material-ui/icons/Map";
import SupervisedUserCircleIcon from "@material-ui/icons/SupervisedUserCircle";
import React from "react";

interface Props extends BottomNavigationProps {
  onSelectionChange: (selected: MenuItem) => void;
  selected: MenuItem;
}

export enum MenuItem {
  MAP = "map",
  SETTINGS = "settings",
}

class Menu extends React.Component<Props> {
  render(): React.ReactNode {
    return (
      <BottomNavigation
        value={this.props.selected}
        onChange={(_, newValue: MenuItem) => {
          this.props.onSelectionChange(newValue);
        }}
      >
        <BottomNavigationAction
          id="mapMenuButton"
          label="Map"
          value={MenuItem.MAP}
          icon={<MapIcon />}
        />
        <BottomNavigationAction
          id="settingsMenuButton"
          label="Settings"
          value={MenuItem.SETTINGS}
          icon={<SupervisedUserCircleIcon />}
        />
      </BottomNavigation>
    );
  }
}

export default Menu;
