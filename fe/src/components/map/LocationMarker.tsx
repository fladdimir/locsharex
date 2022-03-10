import { DragEndEvent, Icon, LatLngExpression, Point } from "leaflet";
import React from "react";
import { Marker, Tooltip } from "react-leaflet";

interface Props {
  name: string;
  position: LatLngExpression;
  icon?: Icon;
  draggable?: boolean;
  onDrop?: (event: DragEndEvent) => void;
}

const defaultIcon = new Icon({
  iconUrl: "icons/baseline_accessibility_new_black_48dp.png",
  iconAnchor: [24, 24],
  iconSize: [48, 48],
});

class LocationMarker extends React.Component<Props> {
  render(): React.ReactNode {
    return (
      <Marker
        position={this.props.position}
        icon={this.props.icon ?? defaultIcon}
        draggable={this.props.draggable}
        eventHandlers={{
          dragend: (event) => {
            if (this.props.onDrop) this.props.onDrop(event);
          },
        }}
      >
        <Tooltip direction="top" permanent={true} offset={new Point(0, -20)}>
          {this.props.name}
        </Tooltip>
      </Marker>
    );
  }
}

export default LocationMarker;
