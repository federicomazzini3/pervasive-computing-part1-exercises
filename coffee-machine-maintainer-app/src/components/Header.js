import React from 'react';

const Header = ({ title, state, onSelect, onMachineList }) => {
    return (
        <header className='header'>
            <h1>{title}</h1>
            {(state === "machineList") &&
                <button
                    onClick={onSelect}
                    style={{ backgroundColor: 'black' }}
                    className='btn'>
                    Add machine
                </button>
            }
            {(state === "machineDetails") &&
                <button
                    onClick={onMachineList}
                    style={{ backgroundColor: 'black' }}
                    className='btn'>
                    back
                </button>
            }
        </header>
    )
};

export default Header;
