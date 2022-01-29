import React from 'react'
import { useState } from 'react'

const Sugar = ({ levels, sugarLevel, onChangeSugarLevel }) => {


    const checked = (level) => {
        if (level === sugarLevel)
            return true
        return false
    }

    return (
        <div className='sugar'>
            <table>
                <tbody>
                    <tr>
                        <td>
                            <h3>Sugar</h3>
                        </td>
                        <td>
                            <form>
                                {levels.map((lv) =>
                                    <span key={lv} style={{ margin: '5px' }}>
                                        <input type="radio" name="sugarLevel" id="level1" value={lv} checked={checked(lv)} onChange={() => onChangeSugarLevel(lv)}></input>
                                        <label>{lv}</label>
                                    </span>
                                )}
                            </form>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}

export default Sugar
