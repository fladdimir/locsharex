import CssBaseline from "@material-ui/core/CssBaseline";
import React from "react";
import Start from "./components/Start";

class App extends React.Component {
  render(): React.ReactNode {
    return (
      <React.Fragment>
        <CssBaseline />
        <Start />
      </React.Fragment>
    );
  }
}

export default App;
