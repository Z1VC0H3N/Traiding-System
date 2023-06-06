import { LoadingButton } from "@mui/lab";
import { Dialog, Box, Grid, Typography, Button, TextField, SelectChangeEvent } from "@mui/material";
import { useCallback, useState } from "react";
import { clearStoreError } from "../../reducers/storesSlice";
import error from "../Alerts/error";
import AlertDialog from "../Dialog/AlertDialog";
import SelectAutoWidth from "../Selectors/AutoWidth";
import { useNavigate } from "react-router-dom";
import { updateService } from "../../types/formsTypes";
import { useAppDispatch, useAppSelector } from "../../redux/store";
import { useForm } from "react-hook-form";

const UpdateServices = () => {
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const form = useForm<updateService>();
    const [service, setService] = useState('');
    const [action, setAction] = useState('');
    const [param, setParam] = useState('');

    const userId = useAppSelector((state) => state.auth.userId);
    const isLoading = useAppSelector((state) => state.admin.isLoading);
    const error = useAppSelector((state) => state.admin.error);

    const suppliers = useAppSelector((state) => state.payment.suppliers);
    const payments = useAppSelector((state) => state.payment.paymentServices);

    const setPayment = () => {
        setService('payment');
    }
    const setSupply = () => {
        setService('supplier');
    }
    const handleChange = (event: SelectChangeEvent) => {
        setParam(event.target.value as string);
    }

    const handleOnClose = useCallback(() => {
        navigate('/dashboard/admin');
        //dispatch(getStore({ userId: userId, storeId: storeId }));
    }, []);
    const handleOnSubmit = () => {
        form.setValue("userId", userId);
        form.setValue("service", service);
        form.setValue("action", action);
        handleOnClose();
    }

    return (
        <>
            <Dialog onClose={handleOnClose} open={true}>
                <Box
                    sx={{
                        marginTop: 4,
                        top: '50%',
                        left: '50%',
                        height: 400,
                        width: '80%',
                        flexDirection: 'column',
                        alignItems: 'center',
                        marginLeft: 'auto',
                        marginRight: 'auto',
                        marginBottom: -2,
                        bgcolor: 'background.paper',

                    }}
                >
                    <Grid
                        spacing={2}
                        container
                        component="form"
                        onSubmit={handleOnSubmit}
                    >
                        <Grid item xs={12}>
                            <Typography component="h1" sx={{ alignContent: 'center', align: 'center', textAlign: 'center' }} >
                                choose service to update
                            </Typography>
                        </Grid>
                        <Box display={'flex'} sx={{ ml: 15 }}>
                            <Button

                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2, marginRight: 2, marginLeft: 2 }}
                                onClick={setSupply}
                                color={service === 'supplier' ? 'success' : 'primary'}
                            >
                                supply
                            </Button>
                            <Button
                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2, marginRight: 2, marginLeft: 2 }}
                                onClick={setPayment}
                                color={service === 'payment' ? 'success' : 'primary'}
                            >
                                payment
                            </Button>
                        </Box>
                        <Grid item xs={12} >
                            <Typography component="h1" sx={{ alignContent: 'center', align: 'center', textAlign: 'center' }} >
                                choose the action
                            </Typography>
                        </Grid>
                        {service != '' ?
                            <Box display={'flex'} sx={{ ml: 15 }}>
                                <Button
                                    fullWidth
                                    variant="contained"
                                    sx={{ mt: 3, mb: 2, marginRight: 2, marginLeft: 2 }}
                                    onClick={() => setAction('add')}
                                    color={action === 'add' ? 'success' : 'primary'}
                                >
                                    add
                                </Button>
                                <Button
                                    fullWidth
                                    variant="contained"
                                    sx={{ mt: 3, mb: 2, marginRight: 2, marginLeft: 2 }}
                                    onClick={() => setAction('remove')}
                                    color={action === 'remove' ? 'success' : 'primary'}
                                >
                                    remove
                                </Button>
                            </Box> : null}
                        {action != '' ?
                            action === 'add' ?
                                <Grid item xs={12}>
                                    <TextField
                                        sx={{ ml: 15 }}
                                        required
                                        id="outlined-required"
                                        label={service === 'supplier' ? "supplier name" : "payment name"}
                                        onChange={(e) => { setParam(e.target.value) }}
                                    />
                                </Grid>
                                : service === 'supplier' ?
                                    <Grid item xs={12}>
                                        <SelectAutoWidth label={'suppliers in system'} values={suppliers} labels={suppliers} value={param} handleChange={handleChange} />
                                    </Grid> : <Grid item xs={12}>
                                        <SelectAutoWidth label={'payments in system'} values={payments} labels={payments} value={param} handleChange={handleChange} />
                                    </Grid>

                            : null}
                        <Grid item xs={12}>
                            <LoadingButton
                                type="submit"
                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2 }}
                                loading={isLoading}
                                disabled={service === '' || action === '' || param === '' ? true : false}
                            >
                                {action === 'add' ? 'add' : action === 'remove' ? 'remove' : 'update'}
                            </LoadingButton>
                        </Grid>
                    </Grid >
                </Box>
            </Dialog >
            {!!error ?
                <AlertDialog open={!!error} onClose={() => { dispatch(clearStoreError({})); }} text={error} sevirity={"error"} />
                : null}
        </>

    )
}
export default UpdateServices;