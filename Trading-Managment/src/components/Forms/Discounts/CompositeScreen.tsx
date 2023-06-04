import { Dialog, Box, Grid, Typography, Button } from "@mui/material";
import { useCallback, useState } from "react";
import { getStore } from "../../../reducers/storesSlice";
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../../../redux/store";
import { LoadingButton } from "@mui/lab";
import ReactFlow, { Edge, OnNodesChange, applyNodeChanges, OnEdgesChange, applyEdgeChanges, Connection, addEdge, Controls, MiniMap, Background } from "reactflow";
import { DiscountNodes } from "../../../types/systemTypes/DiscountNodes";


import 'reactflow/dist/style.css';
import { Node } from 'reactflow';
import Bar2 from "../../Bars/Navbar/NavBar2";
import { reset } from "../../../reducers/discountSlice";
const CompositeScreen = () => {
    const dispatch = useAppDispatch();
    const navigate = useNavigate();
    const userName = useAppSelector(state => state.auth.userName);
    const privateName = userName.split('@')[0];
    const initialNodes = useAppSelector(state => state.discount.discountNodes);
    const initialEdges = useAppSelector(state => state.discount.discountEdges);
    const [nodes, setNodes] = useState<Node<DiscountNodes>[]>(initialNodes);
    const [edges, setEdges] = useState<Edge[]>(initialEdges);


    const handleRegular = () => {
        console.log('regular');
        navigate('addNewRegular');
    }
    const handleComosite = () => {
        console.log('composite');
        navigate('addNewComposite');
    }
    const onNodesChange: OnNodesChange = useCallback(
        (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
        [setNodes]
    );
    const onEdgesChange: OnEdgesChange = useCallback(
        (changes) => setEdges((eds) => applyEdgeChanges(changes, eds)),
        [setEdges]
    );

    const onConnect = useCallback((params: Edge | Connection) => setEdges((eds) => addEdge(params, eds)), [setEdges]);

    return (
        <>
            <Bar2 headLine={`hello ${privateName} , wellcome to `} />
            <Box display='flex'>
                <Button sx={{ mt: 2, mr: 2 }} variant="contained" onClick={handleRegular}>add regular discount</Button>
                <Button sx={{ mt: 2, ml: 2 }} variant="contained" onClick={handleComosite}>add composite discount</Button>
                <Button sx={{ mt: 2, ml: 2 }} variant="contained" onClick={() => dispatch(reset())}>reset</Button>
            </Box>
            <div style={{ width: '100vw', height: '100vh' }}>
                <ReactFlow
                    nodes={nodes}
                    edges={edges}
                    onNodesChange={onNodesChange}
                    onEdgesChange={onEdgesChange}
                    onNodeClick={(event, node) => {
                        console.log(event, node);
                        //navigate(`/discounts/${node.data?.id}`);
                    }}
                    onConnect={onConnect}
                >
                    <Controls />
                    <MiniMap />
                    <Background variant="dots" gap={12} size={1} />
                </ReactFlow>
            </div>
        </>
    );
}
export default CompositeScreen;
