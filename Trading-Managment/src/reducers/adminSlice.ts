import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { LogRecord } from "../types/systemTypes/Log";
import { adminApi } from "../api/adminApi";
import { Complaint } from "../types/systemTypes/Complaint";
import { addAdminParams, answerComplaintParams, cancelMembershipParams, closeStorePerminentlyParams, updateServiceParams } from "../types/requestTypes/adminTypes";
import { ApiError } from "../types/apiTypes";
import { SystemStatus, emptySystemStatus } from "../types/systemTypes/SystemStatus";

interface AdminState {
    isLoading: boolean;
    error: string | null;
    paymentsResponseData: string[];
    suppliersResponseData: string[];
    logRecords: LogRecord[];
    complaints: Complaint[];
    status: SystemStatus;
    msg: string | null;
}
const reducerName = 'adminSlice';
const initialState: AdminState = {
    logRecords: [],
    complaints: [],
    paymentsResponseData: [],
    suppliersResponseData: [],
    status: emptySystemStatus,
    isLoading: false,
    error: null,
    msg: null,
}

export const getLogger = createAsyncThunk<
    LogRecord[],
    number,
    { rejectValue: ApiError }
>(
    `${reducerName}/getLogger`,
    async (adminId, thunkAPI) => {
        return adminApi.getLogger(adminId)
            .then((res) => thunkAPI.fulfillWithValue(res as LogRecord[]))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const getComplaints = createAsyncThunk<
    Complaint[],
    number,
    { rejectValue: ApiError }
>(
    `${reducerName}/getComplaints`,
    async (adminId, thunkAPI) => {
        return adminApi.getComplaints(adminId)
            .then((res) => thunkAPI.fulfillWithValue(res as Complaint[]))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });

export const appointAdmin = createAsyncThunk<
    string,
    addAdminParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/appointAdmin`,
    async (adminParams, thunkAPI) => {
        return adminApi.appointAdmin(adminParams)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const answerComplaint = createAsyncThunk<
    string,
    answerComplaintParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/answerComplaint`,
    async (answerParams, thunkAPI) => {
        return adminApi.answerCompolaint(answerParams)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });

export const adminResign = createAsyncThunk<
    string,
    number,
    { rejectValue: ApiError }
>(
    `${reducerName}/adminResign`,
    async (adminId, thunkAPI) => {
        return adminApi.adminResign(adminId)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });

export const closeStorePerminently = createAsyncThunk<
    string,
    closeStorePerminentlyParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/closeStorePerminently`,
    async (closeStorePerminentlyParams, thunkAPI) => {
        return adminApi.closeStorePerminently(closeStorePerminentlyParams)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });

export const cancelMembership = createAsyncThunk<
    string,
    cancelMembershipParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/cancelMembership`,
    async (cancelMembershipParams, thunkAPI) => {
        return adminApi.cancelMembership(cancelMembershipParams)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const removeUser = createAsyncThunk<
    string,
    string,
    { rejectValue: ApiError }
>(
    `${reducerName}/removeUser`,
    async (userName, thunkAPI) => {
        return adminApi.cleanUser(userName)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const getMarketStatus = createAsyncThunk<
    SystemStatus,
    number,
    { rejectValue: ApiError }
>(
    `${reducerName}/marketStatus`,
    async (adminId, thunkAPI) => {
        return adminApi.marketStatus(adminId)
            .then((res) => thunkAPI.fulfillWithValue(res as SystemStatus))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const updateService = createAsyncThunk<
    string,
    updateServiceParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/updateService`,
    async (updateServiceParams, thunkAPI) => {
        return adminApi.updateService(updateServiceParams)
            .then((res) => thunkAPI.fulfillWithValue(res as string))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const getSuppliersToAdd = createAsyncThunk<
    string[],
    number,
    { rejectValue: ApiError }
>(
    `${reducerName}/getSuppliersToAdd`,
    async (adminId, thunkAPI) => {
        return adminApi.getAdminPaymenysService(adminId)
            .then((res) => thunkAPI.fulfillWithValue(res as string[]))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });
export const getPaymentsToAdd = createAsyncThunk<
    string[],
    number,
    { rejectValue: ApiError }
>(
    `${reducerName}/getPaymentsToAdd`,
    async (adminId, thunkAPI) => {
        return adminApi.getAdminPaymenysService(adminId)
            .then((res) => thunkAPI.fulfillWithValue(res as string[]))
            .catch((err) => thunkAPI.rejectWithValue(err as ApiError));
    });

const { reducer: adminReducer, actions: authActions } = createSlice({
    name: reducerName,
    initialState,
    reducers: {
        clearAdminMsg: (state) => {
            state.msg = '';
        },
        clearAdminError: (state) => {
            state.error = '';
        }
    },
    extraReducers: (builder) => {
        builder.addCase(getLogger.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(getLogger.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.logRecords = payload;
        });
        builder.addCase(getLogger.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during get logger") : payload?.message.data ?? "error during get logger";
        });
        builder.addCase(appointAdmin.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(appointAdmin.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.msg = payload;
        });
        builder.addCase(appointAdmin.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during appoint admin") : payload?.message.data ?? "error during  appoint admin";
        });
        builder.addCase(getComplaints.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(getComplaints.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.complaints = payload;
        });
        builder.addCase(getComplaints.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during get complaints") : payload?.message.data ?? "error during get complaints";
        });
        builder.addCase(answerComplaint.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(answerComplaint.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.msg = payload;
        });
        builder.addCase(answerComplaint.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during answer complaint") : payload?.message.data ?? "error during answer complaint";
        });
        builder.addCase(adminResign.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(adminResign.fulfilled, (state, { payload }) => {
            console.log(payload);
            state.isLoading = false;
            state.msg = payload;
        });
        builder.addCase(adminResign.rejected, (state, { payload }) => {
            console.log(payload);
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during admin resign") : payload?.message.data ?? "error during admin resign";

        });
        builder.addCase(closeStorePerminently.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(closeStorePerminently.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.msg = payload;
        });
        builder.addCase(closeStorePerminently.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during close store perminently") : payload?.message.data ?? "error during close store perminently";
        });
        builder.addCase(removeUser.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(removeUser.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.msg = payload;
        });
        builder.addCase(removeUser.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during remove user") : payload?.message.data ?? "error during remove user";
        });
        builder.addCase(getMarketStatus.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(getMarketStatus.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.status = payload;
        });
        builder.addCase(getMarketStatus.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during market status") : payload?.message.data ?? "error during market status";
        });
        builder.addCase(updateService.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(updateService.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.msg = payload;
        });
        builder.addCase(updateService.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during update service") : payload?.message.data ?? "error during update service";
        });
        builder.addCase(getPaymentsToAdd.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(getPaymentsToAdd.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.paymentsResponseData = payload;
        });
        builder.addCase(getPaymentsToAdd.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error during get payments to add") : payload?.message.data ?? "error during get payments to add";
        });
        builder.addCase(getSuppliersToAdd.pending, (state) => {
            state.isLoading = true;
        });
        builder.addCase(getSuppliersToAdd.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.suppliersResponseData = payload;
        });
        builder.addCase(getSuppliersToAdd.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = state.error ? (state.error + ' , ' + payload?.message.data ?? "error get suppliers to addt") : payload?.message.data ?? "error get suppliers to add";
        });
    }
});
export const { clearAdminMsg, clearAdminError } = authActions;

export default adminReducer;