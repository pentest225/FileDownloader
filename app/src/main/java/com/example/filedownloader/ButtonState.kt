package com.example.filedownloader


sealed class ButtonState(val label:String) {
    object Clicked : ButtonState(label = "clicked")
    object Loading : ButtonState(label = "loading")
    object Completed : ButtonState(label = "completed");

    companion object {
        fun getByLabel(label:String):ButtonState{
            return  when(label){
                "clicked"->Clicked
                "loading"-> Loading
                "completed"->Completed
                else ->Completed
            }

        }
    }

}