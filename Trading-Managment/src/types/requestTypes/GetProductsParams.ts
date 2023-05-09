export interface GetProductsParams {
    id: number,
};
export interface PostProductsParams {
    id: number; //userid
    storeId: number;
    category: string[];
    name: string;
    description: string;
    price: number;
    quantity: number;
    img: string;
    
}
export interface PatchProductsParams {
    id: number; //userid
    storeId: number;
    productId: number;
    category: string[];
    name: string;
    description: string;
    price: number;
    quantity: number;
    img: string;
}
export interface DeleteProductsParams {
    id: number; //userid
    storeId: number;
    productId: number;
}