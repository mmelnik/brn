package com.epam.brn.controller

import com.epam.brn.constant.ExerciseTypeEnum
import com.epam.brn.dto.ExerciseDto
import com.epam.brn.service.ExerciseService
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class ExerciseControllerTest {
    @InjectMocks
    lateinit var exerciseController: ExerciseController
    @Mock
    lateinit var exerciseService: ExerciseService

    @Test
    fun `should get exercises for user and series`() {
        // GIVEN
        val userId: Long = 1
        val seriesId: Long = 1
        val exercise = ExerciseDto(1, 1, "name", "desc", 1, ExerciseTypeEnum.SINGLE_WORDS)
        val listExercises = listOf(exercise)
        Mockito.`when`(exerciseService.findExercisesByUserIdAndSeries(userId, seriesId)).thenReturn(listExercises)
        // WHEN
        @Suppress("UNCHECKED_CAST")
        val actualResultData: List<ExerciseDto> =
            exerciseController.getExercises(userId, seriesId).body?.data as List<ExerciseDto>
        // THEN
        assertTrue(actualResultData.contains(exercise))
        verify(exerciseService).findExercisesByUserIdAndSeries(userId, seriesId)
    }

    @Test
    fun `should get exercise by id`() {
        // GIVEN
        val exerciseID: Long = 1
        val exercise = ExerciseDto(1, 1, "exe", "desc", 1, ExerciseTypeEnum.SINGLE_WORDS)
        Mockito.`when`(exerciseService.findExerciseById(exerciseID)).thenReturn(exercise)
        // WHEN
        @Suppress("UNCHECKED_CAST")
        val actualResultData: ExerciseDto =
            exerciseController.getExercisesByID(exerciseID).body?.data as ExerciseDto
        // THEN
        assertEquals(actualResultData, exercise)
        verify(exerciseService).findExerciseById(exerciseID)
    }
}
