import { Basket } from "./Basket";

export interface Cart
{
    baskets: {storeId: number, basket: Basket}[]
}