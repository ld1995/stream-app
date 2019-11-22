import React, {Component} from 'react';
import Element from "../element/element";
import {MDBCardGroup, MDBContainer} from "mdbreact";

class List extends Component {

    constructor(props) {
        super(props);
        this.state = {
            data: new Map(),
            counter: 0
        }
    }

    updateState(element) {
        this.setState((state) => ({
            data: new Map([...state.data, [element.id, element]]),
            counter: state.counter + 1
        }));
    };

    componentDidMount() {
        this.eventSource = new EventSource("http://localhost:8083/videos");
        this.eventSource.onmessage = (event) => {
            let element = JSON.parse(event.data);
            let savedObj = this.state.data.get(element.id);
            if (!savedObj || JSON.stringify(savedObj) !== JSON.stringify(element)) {
                console.log(element, savedObj);
                this.updateState(element);
            }
        };
    }

    componentWillUnmount() {
        this.eventSource.close();
    }

    render() {
        let listItem = Array.from(this.state.data.values()).map(item => <Element key={item.id} id={item.id}
                                                                                 name={item.name}
                                                                                 timestamp={item.timestamp}/>);
        return (
            <div>
                <h1>{this.state.counter}</h1>
                <MDBContainer>
                    <MDBCardGroup column>
                        {listItem}
                    </MDBCardGroup>
                </MDBContainer>
            </div>
        );
    }
}

export default List;