import { Contact } from "./Contact";

export interface UserWithContacts extends Contact {
  sharedWith: Contact[];
  sharedBy: Contact[];
}
