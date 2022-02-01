import logo from './logo.svg';
import './App.css';
import { useState, useEffect } from 'react';
import Header from './components/Header';
import AddMachine from './components/AddMachine';
import MachineList from './components/MachineList';
import MachineDetails from './components/MachineDetails';
import { WebSocketDemo } from './components/WebSocketDemo';

function App() {

  const [apiAddress, setApiAddress] = useState('http://localhost:8000')
  const [state, setState] = useState("machineList")
  const [showAdd, setShowAdd] = useState(true)
  const [machineListData, setMachineListData] = useState([])
  const [machinesAdded, setMachinesAdded] = useState(0)
  const [selectedMachineId, setSelectedMachineId] = useState("")
  const [eventCounter, setEventCounter] = useState(0)

  useEffect(() => {
    const getMachineList = async () => {
      const machineList = await readProperty("/machines")
      console.log(machineList)
      setMachineListData(machineList)
    }

    getMachineList()

  }, [machinesAdded, eventCounter])

  const readProperty = async (property) => {
    const res = await fetch(apiAddress + property)
    const data = await res.json()
    return data
  }

  const onAddCoffeeMachine = async (machineHost, machinePort) => {
    const res = await fetch(`${apiAddress}/addMachine`, {
      headers: { 'Content-Type': 'application/json' },
      method: 'PUT',
      body: JSON.stringify({ host: machineHost, port: machinePort })
    })
    const data = await res.json()
    if (data.result) {
      alert(data.message)
      setMachinesAdded(machinesAdded + 1)
      toggleAddMachine()
    } else {
      alert(data.message + " because " + data.causes)
    }
    return data
  }

  const onDetail = async (machineId) => {
    setSelectedMachineId(machineId)
    toMachineDetail()
    console.log(`Detail requested for machine ${machineId}`)
  }

  const toMachines = () => {
    setState("machineList");
  }

  const toMachineDetail = () => {
    setState("machineDetails")
  }

  const toggleAddMachine = () => {
    setShowAdd(!showAdd)
  }
  
  const onEvent = () => {
    setEventCounter(eventCounter + 1)
  }

  return (
    <div className="container" >
      <Header title='Maintainer'
        state={state} onSelect={toggleAddMachine} onMachineList={toMachines}/>

      {(showAdd && state == "machineList") &&
        <>
          <AddMachine onAdd={onAddCoffeeMachine} />
        </>
      }

      {(state == "machineList") &&
        <MachineList data={machineListData} onDetail={onDetail} />
      }

      {(state == "machineDetails") &&
        <MachineDetails machineId={selectedMachineId} readProperty={readProperty}/>
      }
      <WebSocketDemo onDetail={onDetail} onEvent={onEvent}></WebSocketDemo>
    </div>

  );
}

export default App;