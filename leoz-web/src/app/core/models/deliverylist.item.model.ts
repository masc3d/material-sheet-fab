import { Parcel } from './parcel.model';
import { Address } from './address.model';
import { Appointment } from './appointment.model';

export interface DeliverylistItem {
  id: number;
  deliveryAppointment?: Appointment;
  deliveryAddress?: Address;
  parcels: Parcel[];
}
