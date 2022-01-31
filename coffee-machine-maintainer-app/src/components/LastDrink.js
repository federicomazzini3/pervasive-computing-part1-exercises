import React from 'react'

const LastDrink = ({ lastDrink }) => {
    return (
        <div className={`info`}>
            <h3>
                Last drink: {lastDrink}
            </h3>
        </div>
    )
}

export default LastDrink
