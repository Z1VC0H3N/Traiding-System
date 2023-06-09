import { Route } from "react-router-dom";

import BiddingCenter from "./intex";
import Lottery from "./Lottery";
import Auctions from "./Auctions";

export const BiddingRoutes = [
    <Route key="routes" path="biddingCenter" element={<BiddingCenter />} >
        <Route path="Lottery" element={<Lottery />} />
        <Route path="Auctions" element={<Auctions />} />
    </Route>
]