import { Geolocation } from './geolocation.model';

export interface Address {
  line1?: string;
  line2?: string;
  line3?: string;
  phoneNumber?: string;
  countryCode?: string;
  zipCode?: string;
  city?: string;
  street?: string;
  streetNo?: string;
  geoLocation?: Geolocation
}
