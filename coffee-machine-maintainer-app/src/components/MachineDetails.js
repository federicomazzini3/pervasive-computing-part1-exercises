import React from 'react';
import { useState, useEffect } from 'react';
import Resources from './Resources';
import LastMantainance from './LastMantainance';
import LastDrink from './LastDrink'

const MachineDetails = ({ machineId, readProperty }) => {


    const [machineDetails, setMachineDetails] = useState({})

    const [visible, setVisible] = useState(false)
    useEffect(() => {
        const getMachineDetails = async () => {
            const machineList = await readProperty(`/machines/${machineId}`)
            console.log(machineList)
            setMachineDetails(machineList)
            setVisible(true)
        }

        getMachineDetails()
        const intervalId = setInterval(getMachineDetails, 5000);


        return () => clearInterval(intervalId);
    }, [])

    return <>
        {(visible) &&
            <>
                <Resources resources={machineDetails.availableResources} />
                <LastDrink lastDrink={machineDetails.lastDrink} />
                <LastMantainance lastMantainance={machineDetails.lastMaintenance} />
            </>
        }
    </>
};

export default MachineDetails;
