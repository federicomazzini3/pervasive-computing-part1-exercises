import React from 'react'

const formatDate = (date) => {
    if (date != null)
        return date.toString().substring(0,10) + " at " + date.toString().substring(11,16)
}

const LastMantainance = ({ lastMantainance }) => {
    return (
        <div className={`info`}>
            <h3>
                Last mantainance: {formatDate(lastMantainance)}
            </h3>
        </div>
    )
}

export default LastMantainance
