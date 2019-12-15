import React, {Component} from 'react';
import Card from "react-bootstrap/Card";
import "./VideoInfo.css"
import Player from "../Player/Player";
import Redirect from "react-router-dom/es/Redirect";

export default class VideoInfo extends Component {

    constructor(props) {
        super(props);
        this.state = {
            isMouseInside: false,
            redirect: false
        };
    }

    handleClick = () => {
        this.setState({redirect: true});
    };

    mouseEnter = () => {
        this.setState({isMouseInside: true});
    };

    mouseLeave = () => {
        this.setState({isMouseInside: false});
    };

    render() {
        return (
            <div onMouseEnter={this.mouseEnter} onMouseLeave={this.mouseLeave} onClick={this.handleClick}>
                {this.state.redirect ? <Redirect to={`/video/${this.props.id}`}/> : ''}
                <Card>
                    <Card.Header>{this.props.name}</Card.Header>
                    <Card.Body>
                        <Player id={this.props.id} type={this.props.extension} height={"100%"}
                                width={"100%"} loop={true} autoplay={this.state.isMouseInside} muted={true}/>
                    </Card.Body>
                    <Card.Footer>
                        <small className="text-muted">Added on {new Date(this.props.timestamp * 1000).toLocaleString()}</small>
                    </Card.Footer>
                </Card>
            </div>
        );
    }
}