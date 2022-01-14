import React from 'react'
import {FiSettings} from 'react-icons/fi'
import {FaRegUserCircle} from 'react-icons/fa'

const Footer = ({onRedirect, state}) => {
    return (
        <footer>
            Unibo 2022
            {state == "user" && 
                <FiSettings 
                style={{color:'#cc2121', cursor:'pointer', marginRight:'10px'}} 
                onClick={onRedirect}
                /> }

            {state == "mantainance" && 
                <FaRegUserCircle 
                style={{color:'#cc2121', cursor:'pointer', marginRight:'10px'}} 
                onClick={onRedirect}
                /> }
        </footer>
    )
}

export default Footer
