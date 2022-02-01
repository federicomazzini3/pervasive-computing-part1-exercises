import React from 'react';
import {BiDetail} from 'react-icons/bi'

const Machine = ({machine, onDetail}) => {
    return (
        <div className={`machine ${!machine.maintenanceNeeded ? '' : 'maintenance'}`}>
            <h3>
                {machine.host + ":" + machine.port}

                <BiDetail
                    style={{ color: 'white', cursor: 'pointer' }}
                    onClick={() => onDetail(machine)}
                />
            </h3>
        </div>
    )
};

export default Machine;
