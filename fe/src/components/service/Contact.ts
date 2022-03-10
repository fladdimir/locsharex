import { LatLng } from "leaflet";

export interface Contact {
  id: any;
  name: string;
  position?: LatLng;
}
