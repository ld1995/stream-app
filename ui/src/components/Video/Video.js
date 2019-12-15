import React, {Component} from 'react';
import Player from "../Player/Player";
import './Video.css';
import VideoList from "../VideoList/VideoList";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Spinner from "react-bootstrap/Spinner";
import Card from "react-bootstrap/Card";

const URL = 'http://localhost:8083/video/';

export default class Video extends Component {

    constructor(props) {
        super(props);
        this.state = {
            data: null
        };
    }

    componentDidMount() {
        fetch(URL + this.props.id)
            .then(response => response.json())
            .then(data => this.setState({data}))
    }

    getContent = (data) => {
        return (<div>
            <Container>
                <Row>
                    <Col sm={8}>
                        <Card>
                            <Card.Header>{data.name}</Card.Header>
                            <Card.Body>
                                <Player id={data.id} controls={true} type={data.extension} subtitles={data.subtitles}
                                        muted={false} height={"100%"} width={"100%"}/>
                            </Card.Body>
                            <Card.Footer>
                                <small className="text-muted">Added
                                    on {new Date(data.timestamp * 1000).toLocaleString()}</small>
                            </Card.Footer>
                        </Card>
                    </Col>
                    <Col sm={4}>
                        <VideoList exclude={this.props.id} numberItemsToDisplay={3}/>
                    </Col>
                </Row>
            </Container>
        </div>)
    };

    render() {
        const {data} = this.state;
        let content = <Spinner animation="border"/>;
        if (data) {
            content = this.getContent(data);
        }
        return (content);
    }
}