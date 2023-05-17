import React, { useEffect, useState } from "react";
import NotificationsOutlinedIcon from '@mui/icons-material/NotificationsOutlined';
import StorefrontIcon from '@mui/icons-material/Storefront';
import PersonOutlineOutlinedIcon from "@mui/icons-material/PersonOutlineOutlined";
import ShoppingCartOutlinedIcon from "@mui/icons-material/ShoppingCartOutlined";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css"
import LogoutIcon from '@mui/icons-material/Logout';
import { Product } from "../../../types/systemTypes/Product";
import { Avatar, Box, Button, Checkbox, Dialog, DialogActions, DialogContent, DialogTitle, IconButton, Typography } from "@mui/material";
import { useAppDispatch, useAppSelector } from "../../../redux/store";
import { getClientData, guestEnter, logout } from "../../../reducers/authSlice";
import { getStore } from "../../../reducers/storesSlice";
import { StoreRole } from "../../../types/systemTypes/StoreRole";
import { clearNotifications } from "../../../reducers/authSlice";

interface Props {
    headLine: string;
}
const Bar: React.FC<Props> = ({ headLine }) => {
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const [storeOpen, setStoreOpen] = useState(false)
    const [profileDialogOpen, setProfileDialogOpen] = useState(false);
    const [notificationDialogOpen, setNotificationDialogOpen] = useState(false);

    const notification = useAppSelector((state) => state.auth.notifications);
    const isLoggedIn = useAppSelector((state) => !!state.auth.token);
    const userId = useAppSelector((state) => state.auth.userId);
    const userName = useAppSelector((state) => state.auth.userName);
    const stores_roles: StoreRole[] = useAppSelector((state) => state.auth.storeRoles);
    const stores_names = useAppSelector((state) => state.auth.storeNames);
    const store_images = useAppSelector((state) => state.auth.storeImgs);
    const token = useAppSelector((state) => state.auth.token) ?? "";

    const cart = useAppSelector((state) => state.cart.responseData);
    const numProductsIncart = cart?.baskets?.reduce((acc, item) => acc + item.products.productsList.length, 0) ?? 0;

    const stores = stores_roles ? stores_roles.map((role, index) => {
        return {
            storeId: role.storeId,
            storeRole: role.storeRole,
            storeName: stores_names[index].storeName,
            storeImg: store_images[index].storeImg,
        }
    }) : [];

    const label = { inputProps: { 'aria-label': 'Checkbox demo' } };

    const handleLogout = () => {
        dispatch(logout(userId));
        navigate('/auth/login');
    };
    const handleChooseStore = (storeNumber: number) => () => {
        dispatch(getStore({ userId: userId, storeId: storeNumber }));
        navigate('shops/superior');
    }
    const handleNotification = () => {
        setNotificationDialogOpen(true);
        //navigate('notifications');
    }
    const handleConfirm = (event: React.ChangeEvent<HTMLInputElement>, idx: number): void => {
        console.log(`confirm message ${idx}`);
    }
    useEffect(() => {
        dispatch(getClientData({ userId: userId, token: token }));
    }, [])
    return (
        <div className="navbar">
            <div className="wrapper">
                <div className="center">
                    <Typography variant="h4" component="h4" className="logo" fontFamily={"Gill Sans"}>
                        {headLine}
                    </Typography>
                </div>
                <div className="right">

                    <div className="item">
                        <Link className="link" to="/">About</Link>
                    </div>
                    <div className="item">
                        <Link className="link" to="/">Contact</Link>
                    </div>

                    <div className="icons">
                        {isLoggedIn &&
                            <>
                                <IconButton onClick={handleLogout}>
                                    <LogoutIcon />
                                </IconButton>
                                <IconButton onClick={() => setStoreOpen(true)}>
                                    <StorefrontIcon />
                                </IconButton>

                                <IconButton onClick={handleNotification}>
                                    <div className="cartIcon">
                                        <NotificationsOutlinedIcon />
                                        <span>{notification.length}</span>
                                    </div>
                                </IconButton>
                                <IconButton onClick={() => {
                                    setProfileDialogOpen(true);
                                }}>
                                    <PersonOutlineOutlinedIcon />
                                </IconButton>
                            </>
                        }
                        {!isLoggedIn &&
                            <>
                                <IconButton onClick={() => {
                                    navigate('/auth/login');
                                }}>
                                    <PersonOutlineOutlinedIcon />
                                </IconButton>
                            </>
                        }
                        <IconButton onClick={() => navigate(`/dashboard/${userId}/cart`)}>
                            <div className="cartIcon">
                                <ShoppingCartOutlinedIcon />
                                <span>{numProductsIncart}</span>
                            </div>
                        </IconButton>


                        <Dialog
                            open={profileDialogOpen}
                            onClose={() => {
                                setProfileDialogOpen(false);
                            }}
                        >
                            <DialogTitle>Profile</DialogTitle>
                            <DialogContent dividers>
                                <Box display="flex" alignItems="center">
                                    <Avatar />
                                    <Box ml={3}>
                                        <Typography>{userName}</Typography>
                                    </Box>
                                </Box>
                            </DialogContent>
                            <DialogActions>
                                <Button
                                    onClick={() => {
                                        setProfileDialogOpen(false);
                                        dispatch(logout(userId));
                                        navigate('/auth/login')
                                    }}
                                >
                                    logout
                                </Button>
                                <Button
                                    onClick={() => {
                                        setProfileDialogOpen(false);
                                    }}
                                >
                                    Cancel
                                </Button>
                            </DialogActions>
                        </Dialog>
                        <Dialog
                            open={storeOpen}
                            onClose={() => {
                                setStoreOpen(false);
                            }}
                        >
                            <DialogTitle>your stores</DialogTitle>
                            <>
                                {stores.map((store) => {
                                    return (
                                        <DialogContent dividers key={store.storeId}>
                                            <Button onClick={handleChooseStore(store.storeId)}>
                                                <Avatar src={store.storeImg} />
                                                <Box ml={3} display={'flex'} key={store.storeId}>
                                                    <Typography sx={{ ml: 2, mr: 3 }}>{store.storeName}</Typography>
                                                    <Typography>{store.storeRole}</Typography>
                                                </Box>
                                            </Button>
                                        </DialogContent>
                                    )
                                }
                                )}
                            </>
                            <DialogActions>
                                <Button
                                    onClick={() => {
                                        navigate('/dashboard/store/new');
                                    }}
                                >
                                    add new store
                                </Button>
                                <Button
                                    onClick={() => {
                                        setStoreOpen(false);
                                    }}
                                >
                                    Cancel
                                </Button>
                            </DialogActions>
                        </Dialog>
                        {/* -------------------------------------notofication---------------- */}
                        <Dialog
                            open={notificationDialogOpen}
                            onClose={() => {
                                setNotificationDialogOpen(false);
                            }}
                        >
                            <DialogTitle>your notifications</DialogTitle>
                            <>
                                {notification.map((not, index) => {
                                    return (
                                        <DialogContent dividers key={index}>
                                            <Box ml={3} display={'flex'} key={index}>
                                                <Typography sx={{ ml: 2, mr: 3 }}>{not}</Typography>
                                                <Checkbox {...label} defaultChecked onChange={(e) => { handleConfirm(e, index) }} />
                                            </Box>
                                        </DialogContent>
                                    )
                                }
                                )}
                            </>
                            <DialogActions>
                                <Button
                                    onClick={() => {
                                        setNotificationDialogOpen(false);
                                        dispatch(clearNotifications());
                                    }}
                                >
                                    close
                                </Button>
                            </DialogActions>
                        </Dialog>
                    </div>
                </div>
            </div>
            {/* {open && <Cart />} */}
        </div >
    );
};
export interface SimpleDialogProps {
    open: boolean;
    onClose: (value: string) => void;
}


export default Bar;