import React from 'react';
import Machine from './Machine';

const MachineList = ({ data, onDetail }) => {
    return (
        <>
            {data.map((machine) =>
                <Machine key={machine.id} machine={machine} onDetail={() => onDetail(machine.id)}/>
            )}
        </>)
};

export default MachineList;
