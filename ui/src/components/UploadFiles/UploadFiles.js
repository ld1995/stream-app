import React, {Component} from "react";
import 'react-dropzone-uploader/dist/styles.css'
import Dropzone from 'react-dropzone-uploader'
import Container from "react-bootstrap/Container";

export default class UploadFiles extends Component {

    constructor(props) {
        super(props);
    }

    handleChangeStatus = ({meta, file}, status) => {
        console.log(status, meta, file)
    };

    sendFile = ({file}) => {
        const formData = new FormData();
        formData.append('files', file);
        const url = 'http://localhost:8082/videos';
        const result = fetch(url, {
            method: 'POST',
            body: formData
        }).then(res => console.log(res));
    };

    handleSubmit = (files, allFiles) => {
        allFiles.forEach(this.sendFile);
        allFiles.forEach(f => f.remove())
    };

    render() {
        return (
            <Container className={"container"}>
                <Dropzone
                    onSubmit={this.handleSubmit}
                    onChangeStatus={this.handleChangeStatus}
                    styles={{dropzone: {minHeight: 500, overflow: 'hidden'}}}
                    accept="video/*"
                />
            </Container>
        )
    }

}