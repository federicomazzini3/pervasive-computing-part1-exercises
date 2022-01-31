import React from 'react';
import {BiDetail} from 'react-icons/bi'

const Machine = ({event, onDetail}) => {
    return (
        <div className={`task`}>
            <h3>
                {event.message}

                <BiDetail
                    style={{ color: 'white', cursor: 'pointer' }}
                    onClick={() => onDetail(event.machineId)}
                />
            </h3>
        </div>
    )
};

export default Machine;
