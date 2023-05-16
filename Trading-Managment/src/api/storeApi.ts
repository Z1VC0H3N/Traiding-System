import { ApiResponseListData, ApiResponse } from "../types/apiTypes";
import { AppointOwnerParams, DeleteStoreParams, GetStoreProducts, GetStoresParams, PatchStoreParams, PostStoreParams, getAppointmentsHistoryParams, patchPermissionsParams } from "../types/requestTypes/storeTypes";
import { Product } from "../types/systemTypes/Product";
import { Store } from "../types/systemTypes/Store";
import { StoreInfo } from "../types/systemTypes/StoreInfo";
import { noAuthApiClient, getApiClient } from "./apiClient";
import { apiErrorHandlerWrapper } from "./util";

export const storeApi =
{
    getStoresInfo: (): Promise<ApiResponseListData<StoreInfo>> =>
        apiErrorHandlerWrapper(noAuthApiClient.get('api/stores/info')),

    getStore: (params: GetStoresParams): Promise<ApiResponse<Store>> =>
        apiErrorHandlerWrapper(getApiClient().get('api/stores', {
            params: params
        })),
    getProducts: (params: GetStoreProducts): Promise<ApiResponseListData<Product>> =>
        apiErrorHandlerWrapper(noAuthApiClient.get(`api/stores/${params.storeId}/products`, { params: params })),

    postStore: (params: PostStoreParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().post('api/stores', params)),

    patchStore: (params: PatchStoreParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().patch(`api/stores/${params.storeId}`, params)),

    deleteStore: (params: DeleteStoreParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().delete(`api/stores/${params.storeId}`, {
            params: params
        })),

    appointOwner: (params: AppointOwnerParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().post(`api/stores/${params.storeId}/appointments/owners`, params)),

    appointManager: (params: AppointOwnerParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().post(`api/stores/${params.storeId}/appointments/managers`, params)),

    fireManager: (params: AppointOwnerParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().delete(`api/stores/${params.storeId}/appointments/managers`, { params: params })),

    fireOwner: (params: AppointOwnerParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().delete(`api/stores/${params.storeId}/appointments/owners`, { params: params })),

    patchPermissions: (params: patchPermissionsParams): Promise<ApiResponse<string>> =>
        apiErrorHandlerWrapper(getApiClient().patch(`api/stores/${params.storeId}/permissions`, params)),
}   