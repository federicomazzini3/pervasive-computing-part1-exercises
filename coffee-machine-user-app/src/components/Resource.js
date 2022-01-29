import React from 'react'

const Resource = ({ resource }) => {
    return (
        <div className={`resource ${ resource.value < 20 ? 'low' : ''}`}>
            <h3>
                {resource.name}
            </h3>
            <h3>
                {resource.value}%
            </h3>
        </div>
    )
}

export default Resource
