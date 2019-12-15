import React, {Component} from 'react';

const URL = "http://localhost:8083/video/file/";

export default class Player extends Component {

    constructor(props) {
        super(props);
        this.state = {}
    }

    componentDidUpdate(prevProps) {
        if (prevProps.autoplay !== this.props.autoplay) {
            const video = document.getElementById(this.props.id);
            if (this.props.autoplay) {
                video.load();
            } else {
                video.pause();
            }
        }
    }

    render() {
        if (Array.isArray(this.props.subtitles) && this.props.subtitles.length > 0) {
            //https://www.w3schools.com/tags/av_prop_texttracks.asp
            let textTracks = document.getElementById(this.props.id).textTracks;
            textTracks.add()
            // <track label="England" kind="subtitles" src="this.props.link" srcLang="en" default />

            // autoplay="autoplay" loop="loop"
        }
        return (
            <div>
                <video id={this.props.id}
                       muted={this.props.muted}
                       loop={this.props.loop}
                       autoPlay={this.props.autoplay}
                       controls={this.props.controls}
                       controlsList="nodownload"
                       height={this.props.height}
                       width={this.props.width}
                       src={URL + this.props.id}>
                </video>
            </div>
        );
    }
}