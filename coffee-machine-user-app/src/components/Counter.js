import React from 'react'

const Counter = ({count}) => {
    return (
        <div className={`info`}>
            <h3>
                Served drinks: {count}
            </h3>
        </div>
    )
}

export default Counter
