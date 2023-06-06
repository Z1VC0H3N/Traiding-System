import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { discountApi } from "../api/discountApi";
import { ApiError } from "../types/apiTypes";
import { Discount, PredicateDataObject, emptyCompositeDiscount, emptyPredicate, emptyRegularDiscount } from "../types/systemTypes/Discount";
import { deleteDiscountParams, postCompositeDicountParams, postRegularDicountParams } from "../types/requestTypes/discountTypes";
import { CompositeDiscountNode, DiscountNodes, RegularDiscountNode } from "../types/systemTypes/DiscountNodes";
import { Edge, Node } from "reactflow";
import { stat } from "fs";

const reducerName = 'discounts';

interface DiscountState {
    discountState: {
        isLoading: boolean;
        responseData?: string | null;
        error: string | null;
    },
    isLoading: boolean;
    responseData: Discount[];
    error: string | null;
    currentRegularDiscount: RegularDiscountNode;
    currentCompositeDiscount: CompositeDiscountNode;
    tmpPredicate: PredicateDataObject;
    first: boolean;
    discountNodes: Node<DiscountNodes>[];
    discountEdges: Edge[];
    target: number;
    level: number;
    source: string;
    predicates: PredicateDataObject[];
};

const initialState: DiscountState = {
    discountState: {
        isLoading: false,
        responseData: null,
        error: null,
    },
    isLoading: false,
    responseData: [],
    error: null,
    currentRegularDiscount: emptyRegularDiscount,
    currentCompositeDiscount: emptyCompositeDiscount,
    tmpPredicate: emptyPredicate,
    first: true,
    discountNodes: [],
    discountEdges: [],
    target: 0,
    level: 0,
    source: "0",
    predicates: []
};


export const getDiscounts = createAsyncThunk<
    Discount[],
    void,
    { rejectValue: ApiError }
>(
    `${reducerName}/get`,
    async (_, thunkApi) => {
        return discountApi.getDiscounts()
            .then((res) => thunkApi.fulfillWithValue(res as Discount[]))
            .catch((res) => thunkApi.rejectWithValue(res as ApiError))
    });

export const addRegularDiscount = createAsyncThunk<
    string,
    postRegularDicountParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/postRegularDiscount`,
    async (formData, thunkApi) => {
        return discountApi.postRegularDiscount(formData)
            .then((res) => thunkApi.fulfillWithValue(res as string))
            .catch((res) => thunkApi.rejectWithValue(res as ApiError))
    });

export const addCompositeDiscount = createAsyncThunk<
    string,
    postCompositeDicountParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/postCompositeDiscount`,
    async (formData, thunkApi) => {
        return discountApi.postCompositeDiscount(formData)
            .then((res) => thunkApi.fulfillWithValue(res as string))
            .catch((res) => thunkApi.rejectWithValue(res as ApiError))
    });

export const deleteDiscount = createAsyncThunk<
    string,
    deleteDiscountParams,
    { rejectValue: ApiError }
>(
    `${reducerName}/delete`,
    async (formData, thunkApi) => {
        return discountApi.deleteDiscount(formData)
            .then((res) => thunkApi.fulfillWithValue(res as string))
            .catch((res) => thunkApi.rejectWithValue(res as ApiError))
    });


const { reducer: discountReducer, actions: discountActions } = createSlice({
    name: reducerName,
    initialState,
    reducers: {

        setSourceToRegularDiscount: (state, { payload }) => {
            //state.discountEdges.push({ id: `e${payload}-${state.target}`, source: `${payload}`, target: `${state.target}` });
            state.target = state.target + 1;
        },
        setpercentageToRegularDiscount: (state, { payload }) => {
            return {
                ...state,
                currentRegularDiscount: {
                    ...state.currentRegularDiscount,
                    percentage: payload
                }
            }
        },
        setDiscountTypeToRegularDiscount: (state, { payload }) => {
            return {
                ...state,
                currentRegularDiscount: {
                    ...state.currentRegularDiscount,
                    discountType: payload
                }
            }
        },
        setProductIdToRegularDiscount: (state, { payload }) => {
            return {
                ...state,
                currentRegularDiscount: {
                    ...state.currentRegularDiscount,
                    prodId: payload
                }
            }
        },
        setCategoryToRegularDiscount: (state, { payload }) => {
            return {
                ...state,
                currentRegularDiscount: {
                    ...state.currentRegularDiscount,
                    discountedCategory: payload
                }
            }
        },
        addPredicateToRegularDiscount: (state, { payload }) => {
            debugger;
            //state.currentRegularDiscount.predicates.push({ predType: payload.predType, params: payload.params, composore: payload.composore });
            return {
                ...state,
                currentRegularDiscount: {
                    ...state.currentRegularDiscount,
                    predicates: [...state.currentRegularDiscount.predicates, payload]
                }
            }
        },
        //-------------------tmp predicate---------------
        setSourceForPredicate: (state, { payload }) => {
            state.source = payload;
        },
        setPredicateTypeToTmpPredicate: (state, { payload }) => {
            return {
                ...state,
                tmpPredicate: {
                    ...state.tmpPredicate,
                    predType: payload
                }
            }
        },
        setParamsToTmpPredicate: (state, { payload }) => {
            return {
                ...state,
                tmpPredicate: {
                    ...state.tmpPredicate,
                    params: payload
                }
            }
        },
        setComposoreToTmpPredicate: (state, { payload }) => {
            return {
                ...state,
                tmpPredicate: {
                    ...state.tmpPredicate,
                    composore: payload
                }
            }
        },
        clearTmpPredicate: (state) => {
            state.tmpPredicate = emptyPredicate;
        },
        //-------------------composite-------------------
        clearDiscountError: (state) => {
            state.discountState.error = null;
        },
        clearDiscountsError: (state) => {
            state.error = null;
        },
        cleanRegularDiscount: (state) => {
            state.currentRegularDiscount = emptyRegularDiscount;
        },
        cleanCompositeDiscount: (state) => {
            state.currentCompositeDiscount = emptyCompositeDiscount;
        },
        addFirstComposite: (state, { payload }) => {
            debugger;
            state.discountNodes.push({ id: '0', position: { x: 500, y: 100 }, data: { label: `Id : 0 , precentage : ${payload.percentage} , type: ${payload.numericType + payload.composoreType}`, percentage: payload.percentage, numericType: payload.numericType, logicalType: payload.composoreType, xorDecidingRule: payload.xorRule } });
            state.level = 1;
            debugger;
            console.log(state.discountNodes);
            console.log(state.discountNodes.find((node) => node.id === '0')?.position?.x);
            state.discountNodes.push({ id: '1', position: { x: (state.discountNodes.find((node) => node.id === '0')?.position?.x ?? 0) - ((200 * Math.pow(2, state.level - 1)) / 2), y: 100 * (state.level + 1) }, data: { ...emptyCompositeDiscount, label: '1' } });
            state.discountNodes.push({ id: '2', position: { x: (state.discountNodes.find((node) => node.id === '0')?.position?.x ?? 0) + ((200 * Math.pow(2, state.level - 1)) / 2), y: 100 * (state.level + 1) }, data: { ...emptyCompositeDiscount, label: '2' } });
            state.level = 2;
            state.discountEdges.push({ id: 'e0-1', source: '0', target: '1' });
            state.discountEdges.push({ id: 'e0-2', source: '0', target: '2' });
            state.target = 3;

        },
        addSecondComposite: (state, { payload }) => {
            //change the father to be a composite
            state.discountNodes.forEach((node) => {
                if (node.id === payload?.rootSource) {
                    node.data = { ...node.data, percentage: payload.percentage, numericType: payload.numericType, logicalType: payload.composoreType, xorDecidingRule: payload.xorRule, label: `${payload?.rootSource} , precentage : ${payload.percentage} , type: ${payload.numericType + payload.composoreType} , xorDecidingRule :${payload.xorRule} ` };
                }
            });
            state.discountNodes.push({ id: String(state.target), position: { x: (state.discountNodes.find((node) => node.id === payload?.rootSource)?.position?.x ?? 0) - ((200 * Math.pow(2, state.level - 1)) / 2), y: (state.discountNodes.find((node) => node.id === payload?.rootSource)?.position?.y ?? 0) + 100 }, data: { ...emptyCompositeDiscount, label: `${state.target}` } });
            state.discountEdges.push({ id: `e${payload?.rootSource}-${state.target}`, source: payload?.rootSource, target: String(state.target) });
            state.target++;
            state.discountNodes.push({ id: String(state.target), position: { x: (state.discountNodes.find((node) => node.id === payload?.rootSource)?.position?.x ?? 0) + ((200 * Math.pow(2, state.level - 1)) / 2), y: (state.discountNodes.find((node) => node.id === payload?.rootSource)?.position?.y ?? 0) + 100 }, data: { ...emptyCompositeDiscount, label: `${state.target}` } });
            state.discountEdges.push({ id: `e${payload?.rootSource}-${state.target}`, source: payload?.rootSource, target: String(state.target) });
            state.target++;
            if (state.target > Math.pow(2, state.level)) {
                state.level++;
            }
        },
        addRegularDiscountToSource: (state, { payload }) => {
            debugger;
            state.discountNodes.forEach((node) => {
                if (node.id === payload.source) {
                    node.data = { ...node.data, percentage: payload.percentage, discountType: payload.discountType, prodId: payload.prodId, discountedCategory: payload.discountedCategory, predicates: [...payload.predicates], label: `${payload?.source} , precentage : ${payload.percentage} , discountType: ${payload.discountType}` };
                    //node.data.predicates = { ...node.data.predicates, ...payload.predicates };
                }
            });
            state.discountNodes.filter((node) => node.id === payload.source)[0].data = { ...state.discountNodes.filter((node) => node.id === payload.source)[0].data, percentage: payload.percentage, discountType: payload.discountType, prodId: payload.prodId, discountedCategory: payload.discountedCategory, predicates: [...payload.predicates], label: `${payload?.source} , precentage : ${payload.percentage} , discountType: ${payload.discountType}` };

        },
        reset: (state) => {
            state.discountNodes = [];
            state.discountEdges = [];
            state.level = 0;
            state.target = 0;
        },

    },
    extraReducers: (builder) => {
        //getDiscounts
        builder.addCase(getDiscounts.pending, (state) => {
            state.isLoading = true;
            state.error = null;
        });
        builder.addCase(getDiscounts.fulfilled, (state, { payload }) => {
            state.isLoading = false;
            state.responseData = payload;
        });
        builder.addCase(getDiscounts.rejected, (state, { payload }) => {
            state.isLoading = false;
            state.error = payload?.message.data ?? "error during getDiscounts";
        });
        //addRegularDiscount
        builder.addCase(addRegularDiscount.pending, (state) => {
            state.discountState.isLoading = true;
            state.discountState.error = null;
        });
        builder.addCase(addRegularDiscount.fulfilled, (state, { payload }) => {
            state.discountState.isLoading = false;
            state.discountState.responseData = payload;
        });
        builder.addCase(addRegularDiscount.rejected, (state, { payload }) => {
            state.discountState.isLoading = false;
            state.discountState.error = payload?.message.data ?? "error during addRegularDiscount";
        });
        //addCompositeDiscount
        builder.addCase(addCompositeDiscount.pending, (state) => {
            state.discountState.isLoading = true;
            state.discountState.error = null;
        });
        builder.addCase(addCompositeDiscount.fulfilled, (state, { payload }) => {
            state.discountState.isLoading = false;
            state.discountState.responseData = payload;
        });
        builder.addCase(addCompositeDiscount.rejected, (state, { payload }) => {
            state.discountState.isLoading = false;
            state.discountState.error = payload?.message.data ?? "error during addCompositeDiscount";
        });
        //deleteDiscount
        builder.addCase(deleteDiscount.pending, (state) => {
            state.discountState.isLoading = true;
            state.discountState.error = null;
        });
        builder.addCase(deleteDiscount.fulfilled, (state, { payload }) => {
            state.discountState.isLoading = false;
            state.discountState.responseData = payload;
        });
        builder.addCase(deleteDiscount.rejected, (state, { payload }) => {
            state.discountState.isLoading = false;
            state.discountState.error = payload?.message.data ?? "error during deleteDiscount";
        });
    },
});

export const {
    setSourceToRegularDiscount,
    setpercentageToRegularDiscount,
    setDiscountTypeToRegularDiscount,
    setProductIdToRegularDiscount,
    setCategoryToRegularDiscount,
    addPredicateToRegularDiscount,
    setPredicateTypeToTmpPredicate,
    setParamsToTmpPredicate,
    setComposoreToTmpPredicate,
    clearTmpPredicate,
    clearDiscountError,
    clearDiscountsError,
    cleanRegularDiscount,
    addFirstComposite,
    addSecondComposite,
    addRegularDiscountToSource,
    setSourceForPredicate,
    reset,
} = discountActions;
export default discountReducer;



