export interface Item {
  name: string;
  description: string;
  price: number;
}

export interface ItemWithId extends Item {
  id: number;
}
