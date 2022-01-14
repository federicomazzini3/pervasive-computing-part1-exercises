import logo from './logo.svg';
import './App.css';
import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import { useNavigate } from 'react-router-dom';
import Header from './components/Header';
import Footer from './components/Footer';
import Sugar from './components/Sugar';
import Menu from './components/Menu';
import Resources from './components/Resources';
import LastDrink from './components/LastDrink';
import LastMantainance from './components/LastMantainance';
import Counter from './components/Counter';
import SelectMachine from './components/SelectMachine';

function App() {

  const [thingAddress, setThingAddress] = useState("")
  const [thingKey, setThingKey] = useState("Select")
  const [state, setState] = useState("select")
  const [showSelect, setShowSelect] = useState(true)
  const [levels, setLevels] = useState([0, 1, 2, 3, 4])
  const [sugarLevel, setSugarLevel] = useState(2)
  const [menuData, setMenuData] = useState([])
  const [availableResources, setAvailableResources] = useState([])
  const [lastDrink, setLastDrink] = useState([])
  const [lastMantainance, setLastMantainance] = useState([])
  const [servedDrinks, setServedDrinks] = useState([])
  const [processingDrink, setProcessingDrink] = useState(
    {
      value: false,
      drink: "",
      message: ""
    })

  useEffect(() => {
    const getMenu = async () => {
      const menuFromServer = await readProperty("possibleDrinks")
      setMenuData(menuFromServer)
    }
    const getAvailableResources = async () => {
      const availableResources = await readProperty("availableResources")
      setAvailableResources(availableResources)
    }
    const getLastDrink = async () => {
      const lastDrink = await readProperty("lastDrink")
      setLastDrink(lastDrink)
    }
    const getLastMantainance = async () => {
      const lastMantainance = await readProperty("lastMantainance")
      setLastMantainance(lastMantainance)
    }
    const getServedDrinks = async () => {
      const servedDrinks = await readProperty("servedCounter")
      setServedDrinks(servedDrinks)
    }
    if (thingAddress != "") {
      getMenu()
      getAvailableResources()
      getLastDrink()
      getLastMantainance()
      getServedDrinks()
    }
  }, [processingDrink, thingAddress])

  const readProperty = async (property) => {
    const res = await fetch(`${thingAddress}/properties/${property}`)
    const data = await res.json()
    return data
  }

  const doAction = async (action, uriVariables) => {
    const res = await fetch(`${thingAddress}/actions/${action}?${uriVariables}`, {
      method: 'POST'
    })
    const data = await res.json()
    return data
  }

  const onChangeSugarLevel = (level) => {
    if (level < levels.length && level >= 0)
      setSugarLevel(level)
  }

  const onBuyDrink = async (drink) => {
    const result = await doAction("makeDrink", `drinkId=${drink.textId}&sugarLevel=${sugarLevel}`)
    processDrink(drink.textId, result.message)
  }

  const processDrink = (drinkId, message) => {
    setProcessingDrink({ "value": true, "message": message, "drink": drinkId })
    waitMakingDrink()
  }

  const waitMakingDrink = () => {
    setTimeout(
      () => setProcessingDrink({ "value": false, "message": "", "drink": "" }),
      1000
    )
  }

  const toggleSelectMachine = () => {
    setShowSelect(!showSelect)
  }

  const onSelectCoffeMachine = (address, key) => {
    toggleSelectMachine()
    setThingAddress(address)
    setThingKey(key)
    if(state == "select")
      setState("user")
  }

  const toUser = () => {
    setState("user")
  }

  const toMantainance = () => {
    setState("mantainance")
  }

  return (
    <div className="container">
      <Header title='Coffee Machine'
        state = {state}
        onSelect={toggleSelectMachine}
        showSelect={showSelect}  
        text={thingKey}/>

      {(state == "select" || showSelect) && 
          <SelectMachine onSelect={onSelectCoffeMachine}/>}

      {state == "user" &&
        <>
          <Sugar levels={levels} sugarLevel={sugarLevel} onChangeSugarLevel={onChangeSugarLevel}></Sugar>
          <Menu data={menuData} onBuy={onBuyDrink} processingData={processingDrink}></Menu>
          <Footer onRedirect={toMantainance} state ={state}></Footer>
        </>}


      {state == "mantainance" &&
        <>
          <Counter count={servedDrinks} />
          <Resources resources={availableResources} />
          <LastDrink lastDrink={lastDrink} />
          <LastMantainance lastMantainance={lastMantainance} />
          <Footer onRedirect={toUser} state ={state}></Footer>
        </>}

    </div>

  );


}

export default App;
