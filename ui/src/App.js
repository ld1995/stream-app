import React from 'react';
import './App.css';
import {BrowserRouter as Router} from "react-router-dom";
import Switch from "react-bootstrap/cjs/Switch";
import Route from "react-router-dom/es/Route";
import MyNavbar from "./components/Navbar/Navbar";
import Video from "./components/Video/Video";
import VideoList from "./components/VideoList/VideoList";
import CardColumns from "react-bootstrap/CardColumns";
import UploadFiles from "./components/UploadFiles/UploadFiles";

const getVideo = ({match}) => {
    const {id} = match.params;
    return <Video id={id}/>
};

const getVideos = () => {
    //numberItemsToDisplay={9}
    return <CardColumns className={"container"}><VideoList numberItemsToDisplay={9}/></CardColumns>;
};

const getUploadForm = () => {
    return <UploadFiles/>
};

function App() {
    return (
        <div>
            <MyNavbar/>
            <Router>
                <Switch>
                    <Route path="/videos" render={getVideos}/>
                    <Route path="/video/:id" render={getVideo}/>
                    <Route path="/upload/video" render={getUploadForm}/>
                </Switch>
            </Router>
        </div>
    );
}

export default App;
