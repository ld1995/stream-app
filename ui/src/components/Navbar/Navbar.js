import Navbar from "react-bootstrap/Navbar";
import React from "react";
import Nav from "react-bootstrap/Nav";

export default function MyNavbar() {
    return (
        <Navbar bg="light" variant="light">
            <Navbar.Brand href="/videos">Video platform</Navbar.Brand>
            <Nav>
                <Nav.Link href="/videos">Home</Nav.Link>
            </Nav>
            <Nav className="mr-auto">
                <Nav.Link href="/upload/video">Upload video</Nav.Link>
            </Nav>
            {/*<Form inline>*/}
            {/*    <Button variant="outline-success">Log in</Button>*/}
            {/*</Form>*/}
        </Navbar>
    );
}