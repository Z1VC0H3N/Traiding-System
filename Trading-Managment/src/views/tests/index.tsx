import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "../../redux/store"
import { deleteStore, getStore, postStore } from "../../reducers/storesSlice";
import { Button } from "@mui/material";
import ErrorAlert from "../../components/Alerts/error";
import SuccessAlert from "../../components/Alerts/success";
import { getProducts, patchProduct, postProduct } from "../../reducers/productsSlice";
import { useState, useRef } from 'react';
import { Connection, hubConnection } from 'signalr-no-jquery';
import axios from "axios";
const Tests: React.FC = () => {
    const dispatch = useAppDispatch();
    const id = 1;
    const error = useAppSelector((state) => state.store.error);
    const message = useAppSelector((state) => state.store.storeState.responseData);
    const producterror = useAppSelector((state) => state.product.productState.error); //this is for single product
    const productmessage = useAppSelector((state) => state.product.productState.responseData);
    //const stores_response = useAppSelector((state) => state.store.responseData);
    //const stores: Store[] = stores_response.data.results ?? [];
    // console.log("stores",stores);
    const handleOnAddStore = () => {
        console.log("front add store");
        dispatch(postStore({ userId: 1, desc: "new store" }));
        // dispatch(getStores());
    }
    const handleOnRemove = () => {
        console.log("front remove store");
        dispatch(deleteStore({ userId: 1, storeId: 0 }))
    }

    const handleOnAddProduct = () => {
        console.log("front add product")
        dispatch(postProduct({ id: 1, storeId: 1, category: [], name: "mazda 3", description: "ziv's mazda", price: 5, quantity: 5, img: "" }))
    }
    const handleOnGetProducts = () => {
        console.log("front get products")
        dispatch(getProducts({ storeId: 1 }))
    }
    const handleOnPatchProduct = () => {
        // console.log("frontpatch product")
        // dispatch(patchProduct({
        //     id: 1, storeId: 1, productId: 1, category: [""], name: "", cription: "",
        //     price: number | null;
        //     quantity: number | null;
        //     img: string | null;
        // }))
    }
    //const retryConnection = useRef(0);

    // const newConnection = hubConnection('http://localhost:4567/', { logging: true, });
    // const hubProxy = newConnection.createHubProxy('NotificationHub');
    // hubProxy.on('firstConnection', () => { });
    // newConnection.start()
    //     .done(() => {
    //         retryConnection.current = 0;
    //     })
    //     .fail((e: any) => {
    //         console.log(e);
    //         console.log("signalR could not connect")
    //     })
    // // set up event listeners i.e. for incoming "message" event
    // hubProxy.on('SendNotification', function (idToSend, message) {

    //     if (idToSend === id) {
    //         console.log("messga",message);
    //         //setNotification(message);
    //     }
    // });
    // newConnection.disconnected(() => {
    //     if (retryConnection.current <= 10) {
    //         retryConnection.current++;
    //         setTimeout(() => {
    //             newConnection.start().done(() => {
    //                 retryConnection.current = 0;
    //             });
    //         }, 10000)
    //     }
    // })

    const [message_notification, setMessage] = useState("");

    useEffect(() => {
        const waitForMessage = async () => {
            try {
                const response = await axios.get("http://localhost:4567/wait");
                if (response.status === 200) {
                    setMessage(response.data);
                }
            } catch (error) {
                console.error(error);
            } finally {
                waitForMessage();
            }
        };
        waitForMessage();
    }, []);

    const sendMessage = async () => {
        try {
            const message = "Hello, world!";
            await axios.post("http://localhost:4567/api/sendMessage", { message: message, id: id });
        } catch (error) {
            console.error(error);
        }
    };
    return (
        <>
            <Button
                type="submit"
                fullWidth
                variant="contained"
                onClick={handleOnAddStore}
                sx={{ color: 'black', '&:hover': { backgroundColor: 'green' }, width: '50%', }}
            >
                {'add store'}
            </Button >
            <Button
                type="submit"
                fullWidth
                variant="contained"
                onClick={handleOnRemove}
                sx={{ color: 'black', '&:hover': { backgroundColor: 'green' }, width: '50%', }}
            >
                {'remove store'}
            </Button >
            <Button
                type="submit"
                fullWidth
                variant="contained"
                onClick={handleOnAddProduct}
                sx={{ color: 'black', '&:hover': { backgroundColor: 'green' }, width: '50%', }}
            >
                {'add product'}
            </Button >
            <Button
                type="submit"
                fullWidth
                variant="contained"
                onClick={handleOnGetProducts}
                sx={{ color: 'black', '&:hover': { backgroundColor: 'green' }, width: '50%', }}
            >
                {'get product'}
            </Button >
            <Button
                type="submit"
                fullWidth
                variant="contained"
                onClick={handleOnGetProducts}
                sx={{ color: 'black', '&:hover': { backgroundColor: 'green' }, width: '50%', }}
            >
                {'patch store'}
            </Button >
            <Button
                type="submit"
                fullWidth
                variant="contained"
                onClick={sendMessage}
                sx={{ color: 'black', '&:hover': { backgroundColor: 'green' }, width: '50%', }}
            >
                {'send message'}
            </Button >
            {error ? <ErrorAlert message={error} /> : null}
            {message ? <SuccessAlert message={message} /> : null}
        </>
    )
}
export default Tests;