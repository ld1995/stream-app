import React, {Component} from 'react';

class Player extends Component {

    constructor(props) {
        super(props);
        this.state = {}
    }

    componentDidMount() {
    }

    render() {
        return (
            <div>
                <video id="video" autoPlay controls controlsList="nodownload">
                    <source src={this.props.link} type={this.props.type}/>
                    {/* SUBTITLE_MEDIA_TYPE = "text/vvt: charset=utf-8";*/}
                    {/*<track label="England" kind="subtitles" src="http://localhost:8080/subs/1/en.vtt" srcLang="en" default/>*/}
                    {/*<track label="Russian" kind="subtitles" src="http://localhost:8080/subs/1/ru.vtt" srcLang="ru"/>*/}
                </video>
            </div>
        );
    }
}

export default Player;