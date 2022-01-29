import React from 'react'

const Header = ({ title, state, onSelect , text}) => {
    return (
        <header className='header'>
            <h1>{title}</h1>
            {(state == "user" || state == "mantainance") &&
                <button
                    onClick={onSelect}
                    style={{ backgroundColor: 'black' }}
                    className='btn'>
                    {text}
                </button>
            }
        </header>
    )
}

export default Header
