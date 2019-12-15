import React, {Component} from 'react';
import VideoInfo from "../VideoInfo/VideoInfo";

const URL = "http://localhost:8083/videos";

class VideoList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            data: new Map(),
        }
    }

    updateState(element) {
        this.setState((state) => ({
            data: new Map([...state.data, [element.id, element]])
        }));
    };

    componentDidMount() {
        this.eventSource = new EventSource(URL);
        this.eventSource.onmessage = (event) => {
            let element = JSON.parse(event.data);
            let savedObj = this.state.data.get(element.id);
            if (!savedObj || JSON.stringify(savedObj) !== JSON.stringify(element)) {
                this.updateState(element);
            }
        };
    }

    componentWillUnmount() {
        this.eventSource.close();
    }

    render() {
        let listItem = Array.from(this.state.data.values())
            .filter(item => item.id !== this.props.exclude)
            .sort((a, b) => b.timestamp - a.timestamp)
            .slice(0, this.props.numberItemsToDisplay)
            .map(item => <VideoInfo key={item.id} id={item.id}
                                    name={item.name}
                                    extension={item.extension}
                                    timestamp={item.timestamp}/>);
        return (
            <div>
                {listItem}
            </div>
        );
    }
}

export default VideoList;