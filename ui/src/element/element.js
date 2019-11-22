import React, {Component} from 'react';
import {MDBCard, MDBCardBody, MDBCardFooter, MDBCardHeader} from "mdbreact";

class Element extends Component {

    constructor(props) {
        super(props);
        this.state = {};
    }

    handleClick = () => {
        console.log('click')
    };

    render() {
        //{"id":"5dcd9f247956a023b1510ac8","author":null,"name":"Rammstein - Radio (Official Making Of)","extension":"mp4","timestamp":1573756708.664000000,"subtitles":[]}
        return (
            <MDBCard onClick={this.handleClick}>
                <MDBCardHeader>{this.props.name}</MDBCardHeader>
                <MDBCardBody>
                    {/*<MDBCardTitle tag="h5"></MDBCardTitle>*/}
                </MDBCardBody>
                <MDBCardFooter small muted>
                    {new Date(this.props.timestamp * 1000).toLocaleString()}
                </MDBCardFooter>
            </MDBCard>
        );
    }
}

export default Element;