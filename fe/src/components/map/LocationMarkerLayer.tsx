import { Icon } from "leaflet";
import React from "react";
import { Contact } from "../service/Contact";
import LocationMarker from "./LocationMarker";

async function fetchLocations(id: number): Promise<PositionsResponse> {
  const url = `/api/users/${id}/locations`;
  return fetch(url).then((response) => {
    if (!response.ok) {
      console.error(response);
      alert("Request failure: " + response.statusText);
    }
    return response.json();
  });
}

const selfIcon = new Icon({
  iconUrl: "icons/baseline_face_black_48dp_48.png",
  iconAnchor: [24, 24],
  iconSize: [48, 48],
});

interface Props {
  userId: number;
  onSelfChange: (self: Contact) => void;
  self?: Contact;
  onOthersChange: (others: Contact[]) => void;
  others: Contact[];
}

interface PositionsResponse {
  self: Contact;
  others: Contact[];
}

class LocationMarkerLayer extends React.Component<Props> {
  componentDidMount() {
    fetchLocations(this.props.userId).then((response) => {
      this.props.onSelfChange(response.self);
      this.props.onOthersChange(response.others ?? []);
    });
  }

  render(): React.ReactNode {
    return (
      <React.Fragment>
        {this.props.others.map((info) => (
          <LocationMarker
            key={info.id}
            name={info.name}
            position={info.position ?? [0, 0]} // filtered above
          />
        ))}
        {this.props.self?.position ? (
          <LocationMarker
            name={this.props.self.name}
            position={this.props.self.position}
            icon={selfIcon}
            draggable={true}
            onDrop={(event) => {
              if (this.props.self) {
                const position = event.target.getLatLng();
                const self: Contact = { ...this.props.self, position };
                this.props.onSelfChange(self);
              }
            }}
          />
        ) : undefined}
      </React.Fragment>
    );
  }
}

export default LocationMarkerLayer;
