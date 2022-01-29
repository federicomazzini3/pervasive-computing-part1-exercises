import React from 'react'
import {FaCoffee} from 'react-icons/fa'
import {GiCoffeeCup} from 'react-icons/gi'

const Drink = ({drink, onBuy, processingData}) => {
    return (
        <div className={`task ${drink.available ? '' : 'reminder'}`}>
            <h3>
                {processingData.value && processingData.drink == drink.textId 
                    ? processingData.message 
                    : drink.name}

                {drink.available && !processingData.value && <GiCoffeeCup 
                style={{color:'white', cursor:'pointer'}} 
                onClick={() => onBuy(drink)}
                />}
            </h3>
        </div>
    )
}

export default Drink
