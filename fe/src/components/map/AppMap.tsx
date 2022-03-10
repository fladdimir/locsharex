import { Container, IconButton, TextField, Tooltip } from "@material-ui/core";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";
import {
  ErrorEvent,
  LatLng,
  LatLngExpression,
  LocationEvent,
  Map,
} from "leaflet";
import React from "react";
import { MapContainer, TileLayer } from "react-leaflet";
import { fetchX } from "../service/fetchX";
import LocationMarkerLayer from "./LocationMarkerLayer";
import { Contact } from "../service/Contact";
interface Props {
  style: React.CSSProperties;
  userId: number;
}

interface State {
  filter: string;
  self?: Contact;
  others: Contact[];
  othersFiltered: Contact[]; // defined position and not filtered by user-search
  pickLocationMode: boolean;
  mobile: boolean;
  gpsEnabled: boolean;
  firstLocate: boolean;
}

function requestPositionUpdate(self: Contact) {
  const url = "/api/users";
  fetchX(url, "PUT", self);
}

function detectMobile(): boolean {
  let check = false;
  (function (a) {
    if (
      /(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(
        a
      ) ||
      /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(
        a.substr(0, 4)
      )
    )
      check = true;
  })(navigator.userAgent || navigator.vendor);
  return check;
}

function isNumeric(n: any) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}

// just start somewhere..
const center: LatLngExpression = [53.554043, 9.928579];
const zoom = 7;

class AppMap extends React.Component<Props, State> {
  state: State = {
    filter: "",
    self: undefined,
    others: [],
    othersFiltered: [],
    pickLocationMode: false,
    mobile: detectMobile(),
    gpsEnabled: false,
    firstLocate: false,
  };
  map: Map | undefined;

  onUserIconDrop(event: any) {
    event.preventDefault();
    const latLng = this.map?.mouseEventToLatLng(event);
    if (this.state.self && latLng) {
      this.state.self.position = latLng;
      this.setState({
        self: this.state.self,
      });
      requestPositionUpdate(this.state.self);
    }
  }

  allowDrag(e: React.DragEvent<HTMLDivElement>) {
    e.preventDefault();
    if (e.dataTransfer) {
      e.dataTransfer.dropEffect = "move";
    }
  }

  enableGps() {
    this.setState({ gpsEnabled: true, firstLocate: true });
    this.map
      ?.locate({ setView: false, watch: true })
      .on("locationfound", (event: LocationEvent) => this.locationFound(event))
      .on("locationerror", (event: ErrorEvent) => {
        alert(`Locate Error:\n\n${event.message}`);
        this.disableGps();
      });
  }

  disableGps() {
    this.setState({ gpsEnabled: false });
    this.map?.stopLocate();
  }

  locationFound(event: LocationEvent) {
    const self = this.state.self;
    if (self) self.position = event.latlng;
    this.setState({ self });
    if (self) requestPositionUpdate(self);
    if (this.state.firstLocate) {
      this.setState({ firstLocate: false });
      this.flyToBounds(this.state.othersFiltered, self);
    }
  }

  flyToBounds(othersFiltered: Contact[], self?: Contact) {
    let positions: (LatLng | undefined)[] = [self?.position];
    positions = positions.concat(othersFiltered.map((o) => o.position));
    const definedPositions: any[] = positions.filter(
      (p: LatLng | undefined) => {
        return p && isNumeric(p.lat) && isNumeric(p.lng); // positions might be undefined
      }
    );
    if (definedPositions && definedPositions.length > 0)
      this.map?.flyToBounds(definedPositions);
  }

  getOthersFiltered(others: Contact[], filter: string): Contact[] {
    return others
      .filter((o) => o.name.toLowerCase().includes(filter.toLowerCase()))
      .filter((o) => o.position);
  }

  componentWillUnmount() {
    this.disableGps();
  }

  getTooltipPickLocation(): string {
    return this.state.mobile
      ? "Click me to choose your location on the map"
      : "Drag me onto the map to update your position";
  }

  getIconPickLocationMode(): string {
    return this.state.pickLocationMode
      ? "icons/sharp_edit_location_black_48dp.png"
      : "icons/sharp_add_location_black_48dp.png";
  }

  onFilterTextFieldChange(
    event: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>
  ): void {
    const filter = event.target.value;
    const othersFiltered = this.getOthersFiltered(this.state.others, filter);
    this.setState({
      filter,
      othersFiltered,
    });
    this.flyToBounds(othersFiltered, this.state.self);
  }

  onSelfChange(self?: Contact) {
    this.setState({ self });
    if (self) requestPositionUpdate(self);
    if (self?.position) this.flyToBounds(this.state.othersFiltered, self);
  }

  onOthersChange(others: Contact[]) {
    const othersFiltered = this.getOthersFiltered(others, this.state.filter);
    this.setState({
      others,
      othersFiltered,
    });
    if (
      (othersFiltered && othersFiltered.length > 0) ||
      this.state.self?.position
    )
      this.flyToBounds(
        othersFiltered,
        this.state.self?.position ? this.state.self : undefined
      );
  }

  render(): React.ReactNode {
    return (
      <div
        style={this.props.style}
        onDragOver={(e) => this.allowDrag(e)}
        onDrop={(event) => this.onUserIconDrop(event)}
        onClick={(event) => {
          if (this.state.pickLocationMode) this.onUserIconDrop(event);
          this.setState({ pickLocationMode: false });
        }}
      >
        <MapContainer
          center={center}
          zoom={zoom}
          scrollWheelZoom={true}
          zoomControl={false}
          style={{ height: "100%" }}
          whenCreated={(map: Map) => (this.map = map)}
        >
          <TileLayer
            attribution='&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            opacity={0.65}
          />
          <LocationMarkerLayer
            userId={this.props.userId}
            self={this.state.self}
            onSelfChange={(self?: Contact) => this.onSelfChange(self)}
            others={this.state.othersFiltered}
            onOthersChange={(others: Contact[]) => this.onOthersChange(others)}
          />
        </MapContainer>
        <Container
          style={{
            position: "absolute",
            top: "30px",
            left: "25px",
            zIndex: 1e10,
            display: "flex",
            alignItems: "start",
            flexDirection: "column",
          }}
        >
          <TextField
            variant="outlined"
            label="Filter..."
            onChange={(event) => this.onFilterTextFieldChange(event)}
            value={this.state.filter}
          />

          {/* gps */}
          {this.state.gpsEnabled ? (
            <Tooltip title="Disable GPS" placement="right">
              <IconButton onClick={() => this.disableGps()}>
                <img
                  src="icons/sharp_near_me_black_48dp.png"
                  style={{ width: "25px", height: "25px" }}
                />
              </IconButton>
            </Tooltip>
          ) : (
            <Tooltip title="Enable GPS" placement="right">
              <IconButton onClick={() => this.enableGps()}>
                <img
                  src="icons/sharp_near_me_disabled_black_48dp.png"
                  style={{ width: "25px", height: "25px" }}
                />
              </IconButton>
            </Tooltip>
          )}

          {/* choose by hand */}
          {!this.state.self?.position ? (
            <Tooltip title={this.getTooltipPickLocation()} placement="right">
              <IconButton
                draggable={true}
                onDragEnd={(event: React.DragEvent) => {
                  event.preventDefault();
                }}
                onClick={(event) => {
                  this.setState({
                    pickLocationMode: !this.state.pickLocationMode,
                  });
                  event.stopPropagation();
                }}
              >
                <img
                  // draggable image to be put onto the map
                  src={this.getIconPickLocationMode()}
                  width="25px"
                  height="25px"
                />
              </IconButton>
            </Tooltip>
          ) : (
            <Tooltip title="Clear your position" placement="right">
              <IconButton
                onClick={() => {
                  if (this.state.self) this.state.self.position = undefined;
                  this.setState({ self: this.state.self });
                  if (this.state.self) requestPositionUpdate(this.state.self);
                }}
              >
                <HighlightOffIcon />
              </IconButton>
            </Tooltip>
          )}
        </Container>
      </div>
    );
  }
}

export default AppMap;
